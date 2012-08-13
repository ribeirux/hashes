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
package org.hashes.collision;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hashes.util.FileUtils;

import com.google.common.base.Charsets;

/**
 * Hash collision generator.
 * <p>
 * All hash collision algorithms must extends this class.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public abstract class AbstractCollisionGenerator {

    private static final Log LOG = LogFactory.getLog(AbstractCollisionGenerator.class);

    /**
     * Generate a list of distinct keys with the same hash code.
     * 
     * @param numberOfKeys number of keys to generate
     * @return a list of distinct keys with the same hash code
     */
    public abstract List<String> generateCollisions(final int numberOfKeys);

    /**
     * Get pre-built collisions file name.
     * 
     * @return the name of the file with pre-built collisions
     */
    protected abstract String getPreBuiltCollisionsFileName();

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
            try {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Loading " + numberOfKeys + " keys");
                }

                // all files with pre-built collisions should use UTF-8 encoding.
                collisions = FileUtils.readLines(this.getPreBuiltCollisionsFileName(), numberOfKeys, Charsets.UTF_8);
            } catch (final IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not load pre-built keys, generating new keys", e);
                }

                collisions = this.generateCollisions(numberOfKeys);
            }
        }

        return collisions;
    }

}
