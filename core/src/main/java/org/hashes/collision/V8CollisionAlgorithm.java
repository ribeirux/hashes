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
 * Google V8 hash collision algorithm.
 * <p>
 * Based on: https://github.com/hastebrot/V8-Hash-Collision-Generator
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class V8CollisionAlgorithm extends AbstractMITMAlgorithm {

    private static final String PRE_BUILT_FILE_NAME = "v8.txt";

    /**
     * Creates a new instance with specified seed.
     * 
     * @param seed MITM seed
     */
    public V8CollisionAlgorithm(final String seed) {
        super(seed);
    }

    @Override
    public int hash(final String key) {
        Preconditions.checkNotNull(key, "key");

        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash += key.charAt(i);
            hash += (hash << 10);
            hash ^= (hash >>> 6);
        }

        return hash;
    }

    @Override
    protected int hashBack(final String key, final int hash) {
        int result = hash;

        for (int i = key.length() - 1; i >= 0; i--) {
            final int part1 = result >>> 26 << 26;
            final int part2 = (result ^ (part1 >>> 6)) >>> 20 << 26 >>> 6;
            final int part3 = (result ^ (part2 >>> 6)) >>> 14 << 26 >>> 12;
            final int part4 = (result ^ (part3 >>> 6)) >>> 8 << 26 >>> 18;
            final int part5 = (result ^ (part4 >>> 6)) >>> 2 << 26 >>> 24;
            final int part6 = (result ^ (part5 >>> 6)) << 30 >>> 30;

            result = (part1 + part2 + part3 + part4 + part5 + part6) * (-1072694271) - key.charAt(i);
        }

        return result;

    }

    @Override
    protected String getPreBuiltCollisionsFileName() {
        return PRE_BUILT_FILE_NAME;
    }
}
