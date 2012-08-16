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

import com.google.common.base.Preconditions;

/**
 * Holds all variables used to establish a HTTP connection.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class HttpHost {

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
     * @param readTImeout connect timeout in milliseconds
     */
    public HttpHost(final Protocol protocol, final String hostname, final int port, final int connectTimeout,
            final int readTImeout) {
        this.protocol = Preconditions.checkNotNull(protocol);
        this.hostname = Preconditions.checkNotNull(hostname, "hostname");
        this.port = port;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTImeout;
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
    public boolean hasDefaultPort() {
        return this.port == this.protocol.getDefaultPort();
    }
}
