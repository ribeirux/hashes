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

import com.google.common.base.Preconditions;

/**
 * DJBX33X hash collision algorithm.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class DJBX33XCollisionAlgorithm extends AbstractMITMAlgorithm {

    private static final String PRE_BUILT_FILE_NAME = "asp.txt";

    /**
     * Creates a new instance with specified seed.
     * 
     * @param seed MITM seed
     */
    public DJBX33XCollisionAlgorithm(final String seed) {
        super(seed);
    }

    @Override
    public int hash(final String key) {
        Preconditions.checkNotNull(key, "key");

        int hash = 5381;
        for (int i = 0; i < key.length(); i++) {
            hash = ((hash << 5) + hash) ^ key.charAt(i);
        }

        return hash;
    }

    @Override
    protected int hashBack(final String key, final int hash) {
        int result = hash;

        for (int i = key.length(); i > 0; i--) {
            result = (result ^ key.charAt(i - 1)) * 1041204193;
        }

        return result;
    }

    @Override
    protected String getPreBuiltCollisionsFileName() {
        return PRE_BUILT_FILE_NAME;
    }
}
