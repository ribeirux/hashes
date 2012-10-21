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
package org.hashes.config;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Holds all variables used to establish a HTTP connection.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public final class HttpHost {

    private static final String HOST_PORT_SEPARATOR = ":";

    private final Protocol protocol;

    private final String hostname;

    private final int port;

    private final int connectTimeout;

    private final int readTimeout;

    /**
     * Creates a new instance with specified fields.
     * 
     * @param protocol the protocol
     * @param hostname target host
     * @param port target port
     * @param connectTimeout read timeout in milliseconds
     * @param readTimeout connect timeout in milliseconds
     */
    public HttpHost(final Protocol protocol, final String hostname, final int port, final int connectTimeout,
            final int readTimeout) {
        Preconditions.checkArgument(port >= 0);
        Preconditions.checkArgument(connectTimeout >= 0, "connectTimeout");
        Preconditions.checkArgument(readTimeout >= 0, "readTImeout");
        this.protocol = Preconditions.checkNotNull(protocol);
        this.hostname = Preconditions.checkNotNull(hostname, "hostname");
        this.port = port;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * Gets the protocol property.
     * 
     * @return the protocol property
     */
    public Protocol getProtocol() {
        return this.protocol;
    }

    /**
     * Gets the hostname property.
     * 
     * @return the hostname property
     */
    public String getHostname() {
        return this.hostname;
    }

    /**
     * Gets the port property.
     * 
     * @return the port property
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Gets the connectTimeout property.
     * 
     * @return the connectTimeout property
     */
    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    /**
     * Gets the readTimeout property.
     * 
     * @return the readTimeout property
     */
    public int getReadTimeout() {
        return this.readTimeout;
    }

    /**
     * Checks if the connection should use default port.
     * 
     * @return true if the connection should use the default port, otherwise false
     */
    public boolean hasDefaultHttpPort() {
        return this.port == this.protocol.getDefaultPort();
    }

    /**
     * Gets the host.
     * 
     * @return the host
     */
    public String getHost() {
        final StringBuilder hostValue = new StringBuilder(this.hostname);
        if (!this.hasDefaultHttpPort()) {
            hostValue.append(HOST_PORT_SEPARATOR);
            hostValue.append(this.port);
        }

        return hostValue.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.protocol, this.hostname, this.port, this.connectTimeout, this.readTimeout);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        final HttpHost other = (HttpHost) obj;

        return Objects.equal(this.protocol, other.protocol) && Objects.equal(this.hostname, other.hostname)
                && this.port == other.port && this.connectTimeout == other.connectTimeout
                && this.readTimeout == other.readTimeout;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("HttpHost [protocol=");
        builder.append(this.protocol);
        builder.append(", hostname=");
        builder.append(this.hostname);
        builder.append(", port=");
        builder.append(this.port);
        builder.append(", connectTimeout=");
        builder.append(this.connectTimeout);
        builder.append(", readTimeout=");
        builder.append(this.readTimeout);
        builder.append("]");
        return builder.toString();
    }

}
