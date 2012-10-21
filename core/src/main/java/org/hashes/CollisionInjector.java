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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
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
import com.google.common.net.HttpHeaders;

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

        // http://www.ietf.org/rfc/rfc2616.txt
        // Each header field consists of a name followed by a colon (":") and the field value. Field names are
        // case-insensitive.
        final Locale locale = Locale.ENGLISH;
        final Map<String, String> defaultHeaders = new LinkedHashMap<String, String>();
        defaultHeaders.put(HttpHeaders.HOST.toLowerCase(locale), this.configuration.getTarget().getHost());
        defaultHeaders.put(HttpHeaders.CONTENT_TYPE.toLowerCase(locale), "application/x-www-form-urlencoded");
        defaultHeaders.put(HttpHeaders.ACCEPT_CHARSET.toLowerCase(locale), this.configuration.getCharset().name());
        defaultHeaders.put(HttpHeaders.CONTENT_LENGTH.toLowerCase(locale), String.valueOf(contentLength));
        defaultHeaders.put(HttpHeaders.USER_AGENT.toLowerCase(locale), "hashes");
        defaultHeaders.put(HttpHeaders.ACCEPT.toLowerCase(locale), "*/*");

        for (final Entry<String, String> externalHeaders : this.configuration.getHeaders().entrySet()) {
            defaultHeaders.put(externalHeaders.getKey().toLowerCase(locale), externalHeaders.getValue());
        }

        for (final Entry<String, String> header : defaultHeaders.entrySet()) {
            payloadBuilder.append(header.getKey());
            payloadBuilder.append(": ");
            payloadBuilder.append(header.getValue());
            payloadBuilder.append("\r\n");
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
