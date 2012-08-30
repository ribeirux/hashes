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
package org.hashes.collision;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hashes.algorithm.HashAlgorithm;
import org.hashes.util.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

/**
 * Hash collision generator.
 * <p>
 * All hash collision algorithms should extends this class.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public abstract class AbstractCollisionGenerator {

    private static final Log LOG = LogFactory.getLog(AbstractCollisionGenerator.class);

    private static final String PRE_BUILT_FILE_REGEX = "(?=[A-Z][a-z])";

    private static final String PRE_BUILT_FILE_SUFIX = ".txt";

    private final HashAlgorithm hashAlgorithm;

    /**
     * Initializes the hash algorithm.
     * 
     * @param hashAlgorithm the hash algorithm
     */
    public AbstractCollisionGenerator(final HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = Preconditions.checkNotNull(hashAlgorithm, "hashAlgorithm");
    }

    /**
     * Gets the hash algorithm.
     * 
     * @return the hash algorithm
     */
    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    /**
     * Load a list of distinct keys with the same hash code.
     * 
     * @param numberOfKeys number of keys to generate
     * @param forceNew forces the generation of new keys instead of using pre-built
     * @return a list of distinct keys with the same hash code
     */
    public List<String> generateCollisions(final int numberOfKeys, final boolean forceNew) {

        List<String> collisions;

        if (forceNew) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Generating " + numberOfKeys + " keys");
            }
            collisions = this.generateCollisions(numberOfKeys);
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("Loading " + numberOfKeys + " keys");
            }

            final Iterator<String> tokens = Splitter.onPattern(PRE_BUILT_FILE_REGEX)
                    .split(this.getClass().getSimpleName()).iterator();
            if (tokens.hasNext()) {
                try {
                    // all files with pre-built collisions should use UTF-8 encoding.
                    collisions = FileUtils
                            .readLines(tokens.next() + PRE_BUILT_FILE_SUFIX, numberOfKeys, Charsets.UTF_8);
                } catch (final IOException e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Could not load pre-built keys, generating new keys", e);
                    }
                    collisions = this.generateCollisions(numberOfKeys);
                }
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not resolve pre-built keys file name, generating new keys");
                }
                collisions = this.generateCollisions(numberOfKeys);
            }
        }

        return collisions;
    }

    /**
     * Generate a list of distinct keys with the same hash code.
     * 
     * @param numberOfKeys number of keys to generate
     * @return a list of distinct keys with the same hash code
     */
    public abstract List<String> generateCollisions(final int numberOfKeys);

}
