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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hashes.algorithm.HashAlgorithm;
import org.hashes.progress.NoProgressMonitorFactory;
import org.hashes.progress.ProgressMonitorFactory;
import org.hashes.progress.ProgressMonitor;
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

    private static final ProgressMonitorFactory MONITOR_FACTORY = new NoProgressMonitorFactory();

    private static final String PRE_BUILT_REGEX = "(?=[A-Z][a-z])";

    private static final String PRE_BUILT_SUFIX = ".txt";

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
     * Generates a {@link List} of distinct keys with the same hash code.
     * <p>
     * Loads prebuilt collisions if available, otherwise generates new collisions.
     * 
     * @param numberOfKeys number of keys to generate.
     * @return a list of distinct keys with the same hash code
     */
    public List<String> generateCollisions(final int numberOfKeys) {
        return this.generateCollisions(numberOfKeys, MONITOR_FACTORY, false);
    }

    /**
     * Generates a {@link List} of distinct keys with the same hash code.
     * <p>
     * Loads prebuilt collisions if available, otherwise generates new collisions.
     * 
     * @param numberOfKeys number of keys to generate.
     * @param monitorFactory progress monitor factory
     * @return a list of distinct keys with the same hash code
     */
    public List<String> generateCollisions(final int numberOfKeys, final ProgressMonitorFactory monitorFactory) {
        return this.generateCollisions(numberOfKeys, monitorFactory, false);
    }

    /**
     * Generates a {@link List} of distinct keys with the same hash code.
     * <p>
     * If <code>forceNew</code> is true, a new {@link List} of collisions is generated, otherwise the prebuilt
     * collisions are loaded if available
     * 
     * @param numberOfKeys number of keys to generate
     * @param forceNew forces the generation of new keys instead of using pre-built
     * @param monitorFactory progress monitor factory
     * @return a list of distinct keys with the same hash code
     */
    public List<String> generateCollisions(final int numberOfKeys, final ProgressMonitorFactory monitorFactory,
            final boolean forceNew) {
        Preconditions.checkArgument(numberOfKeys > 0, "numberOfKeys");
        Preconditions.checkNotNull(monitorFactory, "monitor");

        List<String> collisions;

        if (forceNew) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Generating " + numberOfKeys + " keys");
            }
            collisions = this.generateNewCollisions(numberOfKeys, monitorFactory);
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("Loading " + numberOfKeys + " keys");
            }

            final Iterator<String> tokens = Splitter.onPattern(PRE_BUILT_REGEX).split(this.getClass().getSimpleName())
                    .iterator();

            if (tokens.hasNext()) {

                try {
                    final ProgressMonitor monitor = monitorFactory.createProgressMonitor("Loading", null);

                    try {
                        // all files with prebuilt collisions should use UTF-8 encoding.
                        collisions = FileUtils.readLines(tokens.next() + PRE_BUILT_SUFIX, numberOfKeys, Charsets.UTF_8);
                    } finally {
                        monitor.done();
                    }
                } catch (final Exception e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Could not load pre-built keys, generating new keys", e);
                    }

                    collisions = this.generateNewCollisions(numberOfKeys, monitorFactory);
                }
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not resolve pre-built keys file name, generating new keys");
                }

                collisions = this.generateNewCollisions(numberOfKeys, monitorFactory);
            }
        }

        return collisions;
    }

    protected List<String> generateNewCollisions(final int numberOfKeys, final ProgressMonitorFactory monitorFactory) {

        final ProgressMonitor monitor = monitorFactory.createProgressMonitor("Generating", numberOfKeys);

        try {
            return this.generateNewCollisions(numberOfKeys, monitor);
        } finally {
            monitor.done();
        }
    }

    /**
     * Generate a list of distinct keys with the same hash code.
     * 
     * @param numberOfKeys number of keys to generate
     * @param monitorFactory monitor factory
     * @return a list of distinct keys with the same hash code
     */
    protected abstract List<String> generateNewCollisions(final int numberOfKeys, ProgressMonitor monitor);

}
