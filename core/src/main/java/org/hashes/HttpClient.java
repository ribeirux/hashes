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
package org.hashes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hashes.config.HttpHost;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

/**
 * Http client.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class HttpClient implements Runnable {

    private static final Log LOG = LogFactory.getLog(HttpClient.class);

    private final int requests;

    private final HttpHost target;

    private final byte[] payload;

    private final boolean waitForResponse;

    private final Charset responseCharset;

    /**
     * Creates a new collision injector.
     * 
     * @param requests number of requests to inject
     * @param target the target host
     * @param payload payload to inject
     * @param waitForResponse if true wait for the response else continue the injection without waiting for response
     * @param charset request charset
     */
    public HttpClient(final int requests, final HttpHost target, final byte[] payload, final boolean waitForResponse,
            final Charset charset) {
        this.requests = requests;
        this.target = Preconditions.checkNotNull(target);
        this.payload = Preconditions.checkNotNull(payload, "payload");
        this.waitForResponse = waitForResponse;
        responseCharset = Preconditions.checkNotNull(charset, "responseCharset");
    }

    @Override
    public void run() {
        for (int i = 0; i < requests; i++) {

            Socket socket = null;
            OutputStream output = null;
            InputStream input = null;

            try {
                socket = createUnconnectedSocket();
                applySettings(socket);
                socket.connect(new InetSocketAddress(target.getHostname(), target.getPort()),
                        target.getConnectTimeout());

                // write the entire byte array in one shot and close the stream.
                // In this situation, buffering would add extra overhead!
                output = socket.getOutputStream();
                output.write(payload);
                output.flush();

                if (waitForResponse && LOG.isInfoEnabled()) {
                    input = socket.getInputStream();
                    LOG.info(new String(ByteStreams.toByteArray(input), responseCharset));
                }
            } catch (final Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("An error occurred while injecting payload. " + e.getMessage(), e);
                }
            } finally {
                closeQuietly(output, input, socket);
            }
        }
    }

    private void closeQuietly(final OutputStream output, final InputStream input, final Socket socket) {

        if (output != null) {
            try {
                output.close();
            } catch (final IOException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Could not close client output stream. " + e.getMessage(), e);
                }
            }
        }

        if (input != null) {
            try {
                input.close();
            } catch (final IOException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Could not close client input stream. " + e.getMessage(), e);
                }
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (final IOException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Could not close client socket. " + e.getMessage(), e);
                }
            }
        }
    }

    protected Socket createUnconnectedSocket() throws IOException {
        return new Socket();
    }

    protected void applySettings(final Socket socket) throws SocketException {
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(target.getConnectTimeout());
    }
}