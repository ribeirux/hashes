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
package org.hashes.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;

/**
 * Holds the logic of looking up a file, in the following sequence:
 * <ol>
 * <li>try to load it with the current thread's context ClassLoader</li>
 * <li>if fails, try to load it as a file from the disk</li>
 * </ol>
 * 
 * @author ribeirux
 * @version $Revision$
 */
public final class FileUtils {

    private static final Log LOG = LogFactory.getLog(FileUtils.class);

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private FileUtils() {
    }

    /**
     * Holds the logic of looking up a file, in the following sequence:
     * <ol>
     * <li>try to load it with the current thread's context ClassLoader</li>
     * <li>if fails, try to load it as a file from the disk</li>
     * </ol>
     * 
     * @param filename might be the name of the file (too look it up in the class path) or an url to a file.
     * @return an input stream to the file or
     * @throws IOException if an I/O error occurs
     */
    public static InputStream lookupFile(final String filename) throws IOException {
        Preconditions.checkNotNull(filename, "filename");

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream stream = classLoader.getResourceAsStream(filename);

        if (stream == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(MessageFormat.format(
                        "Unable to find file {0} in classpath. Searching for this file on the filesystem instead.",
                        filename));
            }

            stream = new FileInputStream(filename);

        }

        return stream;
    }

    /**
     * Writes the <code>toString()</code> value of each item in a collection to the specified {@link File} line by line,
     * using the specified character encoding.
     * 
     * @param file destination file
     * @param collisions collision to write
     * @param charset the encoding to use
     * @throws IOException if an I/O error occurs
     */
    public static void writeLines(final File file, final Collection<?> collisions, final Charset charset)
            throws IOException {
        Preconditions.checkNotNull(file, "file");
        Preconditions.checkNotNull(collisions, "collisions");
        Preconditions.checkNotNull(charset, "charset");

        final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file, false));

        try {
            for (final Object key : collisions) {
                out.write((key.toString() + LINE_SEPARATOR).getBytes(charset));
            }

            // don't swallow close exception if copy completes normally
            out.close();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Could not close output stream for file: " + file.getPath(), e);
                    }
                }
            }
        }
    }

    /**
     * Get the contents of the specified <code>fileName</code> until <code>numberOfLines</code> is reached as a list of
     * Strings, one entry per line, using the specified <code>charset</code>
     * <p>
     * This method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
     * 
     * @param fileName name of the file
     * @param numberOfLines number of lines to read
     * @param charset the encoding to use
     * @return the {@link List} of lines
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readLines(final String fileName, final int numberOfLines, final Charset charset)
            throws IOException {
        Preconditions.checkNotNull(fileName, "fileName");
        Preconditions.checkArgument(numberOfLines > 0, "numberOfKeys");
        Preconditions.checkNotNull(charset, "charset");

        final InputStream prebuilt = FileUtils.lookupFile(fileName);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(prebuilt, charset));

        final List<String> list = new ArrayList<String>(numberOfLines);

        try {
            String line = reader.readLine();
            for (int i = 0; (i < numberOfLines) && (line != null); i++) {
                list.add(line);
                line = reader.readLine();
            }
        } finally {
            try {
                reader.close();
            } catch (final IOException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Could not close reader of file: " + fileName, e);
                }
            }
        }

        return list;
    }
}
