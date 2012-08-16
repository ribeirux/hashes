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

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hashes.config.HttpHost;
import org.hashes.util.NaiveX509TrustManager;

/**
 * Https client.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class HttpsClient extends HttpClient {

    private static final Log LOG = LogFactory.getLog(HttpsClient.class);

    private static final String SSL_CONTEXT_PROTOCOL = "SSL";

    private final SocketFactory socketFactory;

    /**
     * Creates a new collision injector.
     * 
     * @param requests number of requests to inject
     * @param target the target host
     * @param payload payload to inject
     * @param waitForResponse if true wait for the response else continue the injection without waiting for response
     * @param charset request charset
     */
    public HttpsClient(final int requests, final HttpHost target, final byte[] payload, final boolean waitForResponse,
            final Charset charset) {
        super(requests, target, payload, waitForResponse, charset);
        this.socketFactory = buildSocketFactory();
    }

    @Override
    protected Socket createUnconnectedSocket() throws IOException {
        return this.socketFactory.createSocket();
    }

    private static SocketFactory buildSocketFactory() {
        SocketFactory socketFactory;

        try {
            final SSLContext sslContext = SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
            sslContext.init(null, new TrustManager[] { new NaiveX509TrustManager() }, null);
            socketFactory = sslContext.getSocketFactory();
        } catch (final Exception e) {
            LOG.warn("Could not disable certificate validation", e);
            socketFactory = SSLSocketFactory.getDefault();
        }

        return socketFactory;
    }

}
