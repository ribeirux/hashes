/**
 *    Copyright 2012 Pedro Ribeiro
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.hashes;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hashes.config.Configuration;
import org.hashes.config.HttpHost;
import org.hashes.config.Protocol;
import org.hashes.progress.ProgressMonitorFactory;
import org.hashes.util.FileUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Collision injector.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class CollisionInjector {

    private static final Log LOG = LogFactory.getLog(CollisionInjector.class);

    private final Configuration configuration;

    /**
     * Creates a new instance with specified configuration.
     * 
     * @param configuration the configuration
     */
    public CollisionInjector(final Configuration configuration) {
        this.configuration = Preconditions.checkNotNull(configuration, "configuration");
    }

    /**
     * Gets the configuration property.
     * 
     * @return the configuration property
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Starts injecting collisions.
     */
    public void start() {

        final ProgressMonitorFactory factory = this.configuration.getProgressMonitorFactory();
        final int numberOfKeys = this.configuration.getNumberOfKeys();
        final boolean newKeys = this.configuration.isGenerateNewKeys();

        final List<String> collisions = this.configuration.getCollisionGenerator().generateCollisions(numberOfKeys,
                factory, newKeys);

        this.saveCollisions(collisions);

        final byte[] payload = this.buildPayload(collisions);

        final int numberOfClients = this.configuration.getNumberOfClients();
        final Builder<Runnable> clients = ImmutableList.builder();

        for (int i = 0; i < numberOfClients; i++) {
            clients.add(this.createClient(payload));
        }

        this.runClients(clients.build());
    }

    protected void saveCollisions(final List<String> collisions) {

        final File toSave = this.configuration.getCollisionsFile();
        if (toSave != null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Saving collisions to file: " + toSave.getPath());
            }

            try {
                FileUtils.writeLines(toSave, collisions, this.configuration.getCharset());
            } catch (final IOException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Could not save collisions to file: " + toSave.getAbsolutePath(), e);
                }
            }
        }
    }

    protected byte[] buildPayload(final List<String> collisions) {

        final String body = this.buildMessageBody(collisions);

        final StringBuilder payloadBuilder = new StringBuilder();
        this.addRequestLine(payloadBuilder);
        this.addRequestHeaders(body.length(), payloadBuilder);

        payloadBuilder.append(body);

        return payloadBuilder.toString().getBytes(this.configuration.getCharset());
    }

    protected void addRequestLine(final StringBuilder payloadBuilder) {

        payloadBuilder.append("POST ");
        payloadBuilder.append(this.configuration.getPath());
        payloadBuilder.append(" HTTP/1.1\r\n");
    }

    protected void addRequestHeaders(final int contentLength, final StringBuilder payloadBuilder) {

        final Set<String> keys = new HashSet<String>();

        final HttpHost target = this.configuration.getTarget();

        final String hostKey = "host";
        payloadBuilder.append(hostKey);
        payloadBuilder.append(": ");
        payloadBuilder.append(target.getHostname());
        if (!target.hasDefaultPort()) {
            payloadBuilder.append(":");
            payloadBuilder.append(target.getPort());
        }
        payloadBuilder.append("\r\n");
        keys.add(hostKey);

        // content type
        final String contentTypeKey = "content-type";
        payloadBuilder.append(contentTypeKey);
        payloadBuilder.append(": application/x-www-form-urlencoded");
        payloadBuilder.append("\r\n");
        keys.add(contentTypeKey);

        // accept charset
        final String acceptCharsetKey = "accept-charset";
        payloadBuilder.append(acceptCharsetKey);
        payloadBuilder.append(": ");
        payloadBuilder.append(this.configuration.getCharset().name());
        payloadBuilder.append("\r\n");
        keys.add(acceptCharsetKey);

        // content length
        final String contentLengthKey = "content-length";
        payloadBuilder.append(contentLengthKey);
        payloadBuilder.append(": ");
        payloadBuilder.append(contentLength);
        payloadBuilder.append("\r\n");
        keys.add(contentLengthKey);

        // add additional headers
        final Map<String, String> additionalHeaders = this.configuration.getHeaders();
        for (final Entry<String, String> header : additionalHeaders.entrySet()) {
            if (keys.contains(header.getKey())) {
                LOG.warn("Could not override header: " + header.getKey());
            } else {
                payloadBuilder.append(header.getKey());
                payloadBuilder.append(": ");
                payloadBuilder.append(header.getValue());
                payloadBuilder.append("\r\n");
            }
        }

        payloadBuilder.append("\r\n");
    }

    protected String buildMessageBody(final List<String> collisions) {

        final StringBuilder payloadBuilder = new StringBuilder();

        final String charsetName = this.configuration.getCharset().name();

        final Iterator<String> iterator = collisions.iterator();

        try {
            if (iterator.hasNext()) {
                payloadBuilder.append(URLEncoder.encode(iterator.next(), charsetName));
                payloadBuilder.append("=");
            }

            while (iterator.hasNext()) {
                payloadBuilder.append("&");
                payloadBuilder.append(URLEncoder.encode(iterator.next(), charsetName));
                payloadBuilder.append("=");
            }
        } catch (final UnsupportedEncodingException e) {
            throw new UnsupportedPayloadEncodingException(e);
        }

        return payloadBuilder.toString();
    }

    protected Runnable createClient(final byte[] payload) {
        final HttpHost target = this.configuration.getTarget();

        Runnable client;
        if (target.getProtocol() == Protocol.HTTP) {
            client = new HttpClient(//
                    this.configuration.getRequestsPerClient(), //
                    target, //
                    payload, //
                    this.configuration.isWaitResponse(), //
                    this.configuration.getCharset());
        } else if (target.getProtocol() == Protocol.HTTPS) {
            client = new HttpsClient(//
                    this.configuration.getRequestsPerClient(), //
                    target, //
                    payload, //
                    this.configuration.isWaitResponse(), //
                    this.configuration.getCharset());
        } else {
            throw new UnsupportedOperationException("Client not implemented for protocol: " + target.getProtocol());
        }

        return client;
    }

    /**
     * Run the specified clients on each thread.
     * 
     * @param clients clients to run
     */
    protected void runClients(final List<Runnable> clients) {
        Preconditions.checkNotNull(clients, "clients");

        final int numberOfClients = clients.size();

        if (LOG.isInfoEnabled()) {
            LOG.info("Starting " + clients.size() + " client(s)");
        }

        final ExecutorService executor = Executors.newFixedThreadPool(numberOfClients);
        try {
            for (final Runnable client : clients) {
                executor.execute(client);
            }
        } finally {
            executor.shutdown();
        }
    }
}
