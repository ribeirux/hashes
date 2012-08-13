/*******************************************************************************
 *
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
 *    
 *******************************************************************************/
package org.hashes.config;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hashes.collision.AbstractCollisionGenerator;
import org.hashes.collision.DJBX33ACollisionGenerator;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * Contains all available configurations.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public final class Configuration {

    private final Charset charset;

    private final Locale locale;

    // mandatory
    private final HttpHost target;

    private final Map<String, String> headers;

    // optional
    private final String path;

    private final AbstractCollisionGenerator collisionGenerator;

    private final File collisionsFile;

    private final boolean waitResponse;

    private final boolean generateNewKeys;

    private final int numberOfKeys;

    private final int requestsPerClient;

    private final int numberOfClients;

    /**
     * Configuration builder.
     * 
     * @author ribeirux
     * @version $Revision$
     */
    public static class ConfigurationBuilder {

        private final Charset charset = Charsets.UTF_8;

        private final Locale locale = Locale.ENGLISH;

        // mandatory
        private final String hostname;

        private final Map<String, String> headers;

        // optional
        private Protocol protocol = Protocol.HTTP;

        private int port = 80;

        private String path = "/";

        private AbstractCollisionGenerator collisionGenerator = new DJBX33ACollisionGenerator();

        private File collisionsFile = null;

        private boolean waitResponse = false;

        private boolean generateNewKeys = false;

        private int numberOfKeys = 85000;

        private int requestsPerClient = 1;

        private int numberOfClients = 1;

        private int connectTimeout = 60;

        private int readTimeout = 60;

        /**
         * Creates a new builder with mandatory fields.
         * 
         * @param hostname target host.
         */
        public ConfigurationBuilder(final String hostname) {
            this.hostname = Preconditions.checkNotNull(hostname, "hostname");
            headers = createDefaultHeaders();
        }

        private Map<String, String> createDefaultHeaders() {
            final Map<String, String> defaultHeaders = new HashMap<String, String>();

            defaultHeaders.put("user-agent", "hashes");
            defaultHeaders.put("accept", "*/*");

            return defaultHeaders;
        }

        /**
         * Defines the scheme to use and the port.
         * <p>
         * Default: HTTP
         * 
         * @param schemeName the scheme name
         * @return the configuration builder
         */
        public ConfigurationBuilder withSchemeName(final String schemeName) {
            Preconditions.checkNotNull(schemeName, "schemeName");
            protocol = Protocol.fromSchemeName(schemeName.toLowerCase(locale));
            port = protocol.getDefaultPort();

            return this;
        }

        /**
         * Defines the target port.
         * 
         * @param port the target port
         * @return the configuration builder
         */
        public ConfigurationBuilder withPort(final int port) {
            this.port = port;

            return this;
        }

        /**
         * Defines the request path.
         * 
         * @param path the request path
         * @return the configuration builder
         */
        public ConfigurationBuilder withPath(final String path) {
            this.path = Preconditions.checkNotNull(path, "path");

            return this;
        }

        /**
         * Sets the collision generator algorithm.
         * <p>
         * Default: DJBX33ACollisionGenerator
         * 
         * @param collisionGenerator the collision generator algorithm.
         * @return the configuration builder
         */
        public ConfigurationBuilder withCollisionGenerator(final AbstractCollisionGenerator collisionGenerator) {
            this.collisionGenerator = Preconditions.checkNotNull(collisionGenerator, "collisionGenerator");

            return this;
        }

        /**
         * Save the collisions to the specified {@link File}.
         * <p>
         * Default: collisions will not saved to file
         * 
         * @param collisionsFile the name of the file
         * @return the configuration builder
         */
        public ConfigurationBuilder saveCollisionsToFile(final File collisionsFile) {
            this.collisionsFile = Preconditions.checkNotNull(collisionsFile, "collisionsFile");

            return this;
        }

        /**
         * Wait for response.
         * <p>
         * Default: false
         * 
         * @return the configuration builder.
         */
        public ConfigurationBuilder waitForResponse() {
            waitResponse = true;

            return this;
        }

        /**
         * Instead of using pre-built keys, generate new ones.
         * 
         * @return the configuration builder.
         */
        public ConfigurationBuilder generateNewKeys() {
            generateNewKeys = true;

            return this;
        }

        /**
         * Inject the specified number of colliding keys for each request.
         * <p>
         * Default: 85000
         * 
         * @param numberOfKeys number of keys to injects for each request
         * @return the configuration builder.
         */
        public ConfigurationBuilder withNumberOfKeys(final int numberOfKeys) {
            Preconditions.checkArgument(numberOfKeys > 0, "numberOfKeys");
            this.numberOfKeys = numberOfKeys;

            return this;
        }

        /**
         * Number of requests per client.
         * <p>
         * Default: 1
         * 
         * @param requestsPerClient the number of requests per client
         * @return the configuration builder.
         */
        public ConfigurationBuilder withRequestsPerClient(final int requestsPerClient) {
            Preconditions.checkArgument(requestsPerClient > 0, "requestsPerClient");
            this.requestsPerClient = requestsPerClient;

            return this;
        }

        /**
         * Number of clients.
         * <p>
         * Default: 1
         * 
         * @param numberOfClients the number of clients
         * @return the configuration builder.
         */
        public ConfigurationBuilder withNumberOfClients(final int numberOfClients) {
            Preconditions.checkArgument(numberOfClients >= 0, "numberOfClients");
            this.numberOfClients = numberOfClients;

            return this;
        }

        /**
         * Connect timeout.
         * 
         * @param connectTimeout connect timeout in seconds.
         * @return the configuration builder.
         */
        public ConfigurationBuilder withConnectTimeout(final int connectTimeout) {
            Preconditions.checkArgument(connectTimeout >= 0, "connectTimeout");
            this.connectTimeout = connectTimeout;

            return this;
        }

        /**
         * Read timeout.
         * 
         * @param readTimeout read timeout in seconds.
         * @return the configuration builder.
         */
        public ConfigurationBuilder withReadTimeout(final int readTimeout) {
            Preconditions.checkArgument(readTimeout > 0, "readTimeout");
            this.readTimeout = readTimeout;

            return this;
        }

        /**
         * Add or replace a HTTP header.
         * 
         * @param key the Header key
         * @param value Header value
         * @return the configuration builder
         */
        public ConfigurationBuilder withHTTPHeader(final String key, final String value) {
            headers.put(//
                    Preconditions.checkNotNull(key, "key").toLowerCase(locale), //
                    Preconditions.checkNotNull(value, "value"));

            return this;
        }

        /**
         * Build immutable configuration.
         * 
         * @return the configuration.
         */
        public Configuration build() {
            return new Configuration(this);
        }

    }

    private Configuration(final ConfigurationBuilder builder) {
        charset = builder.charset;
        locale = builder.locale;
        headers = new ImmutableMap.Builder<String, String>().putAll(builder.headers).build();
        path = builder.path;
        collisionGenerator = builder.collisionGenerator;
        collisionsFile = builder.collisionsFile;
        waitResponse = builder.waitResponse;
        generateNewKeys = builder.generateNewKeys;
        numberOfKeys = builder.numberOfKeys;
        requestsPerClient = builder.requestsPerClient;
        numberOfClients = builder.numberOfClients;
        target = new HttpHost(builder.protocol, builder.hostname, builder.port, builder.connectTimeout,
                builder.readTimeout);
    }

    /**
     * Gets the charset property.
     * 
     * @return the charset property
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Gets the locale property.
     * 
     * @return the locale property
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the target property.
     * 
     * @return the target property
     */
    public HttpHost getTarget() {
        return target;
    }

    /**
     * Gets the headers property.
     * 
     * @return the headers property
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets the path property.
     * 
     * @return the path property
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the collisionGenerator property.
     * 
     * @return the collisionGenerator property
     */
    public AbstractCollisionGenerator getCollisionGenerator() {
        return collisionGenerator;
    }

    /**
     * Gets the collisionsFile property.
     * 
     * @return the collisionsFile property
     */
    public File getCollisionsFile() {
        return collisionsFile;
    }

    /**
     * Gets the waitResponse property.
     * 
     * @return the waitResponse property
     */
    public boolean isWaitResponse() {
        return waitResponse;
    }

    /**
     * Gets the generateNewKeys property.
     * 
     * @return the generateNewKeys property
     */
    public boolean isGenerateNewKeys() {
        return generateNewKeys;
    }

    /**
     * Gets the numberOfKeys property.
     * 
     * @return the numberOfKeys property
     */
    public int getNumberOfKeys() {
        return numberOfKeys;
    }

    /**
     * Gets the requestsPerClient property.
     * 
     * @return the requestsPerClient property
     */
    public int getRequestsPerClient() {
        return requestsPerClient;
    }

    /**
     * Gets the numberOfClients property.
     * 
     * @return the numberOfClients property
     */
    public int getNumberOfClients() {
        return numberOfClients;
    }
}
