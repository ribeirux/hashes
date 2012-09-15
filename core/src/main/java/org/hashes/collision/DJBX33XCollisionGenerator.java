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

import org.hashes.algorithm.DJBX33XHashAlgorithm;

import com.google.common.base.Preconditions;

/**
 * DJBX33X hash collision generator.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class DJBX33XCollisionGenerator extends AbstractMITMGenerator {

    /**
     * Creates a new instance with specified seed.
     * 
     * @param seed MITM seed
     */
    public DJBX33XCollisionGenerator(final String seed) {
        super(new DJBX33XHashAlgorithm(), seed);
    }

    /**
     * Creates a new instance with specified seed.
     * 
     * @param seed MITM seed
     * @param workerThreads number of worker threads, If null the number of available processors is used
     */
    public DJBX33XCollisionGenerator(final String seed, final Integer workerThreads) {
        super(new DJBX33XHashAlgorithm(), seed, workerThreads);
    }

    @Override
    protected int hashBack(final String key, final int end) {
        Preconditions.checkNotNull(key, "key");

        int result = end;
        for (int i = key.length(); i > 0; i--) {
            result = (result ^ key.charAt(i - 1)) * 1041204193;
        }

        return result;
    }
}
