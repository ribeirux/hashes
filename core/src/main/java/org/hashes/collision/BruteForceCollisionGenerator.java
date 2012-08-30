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

import java.util.List;

import org.hashes.algorithm.HashAlgorithm;

/**
 * Brute force collision generator.
 * <p>
 * Created only for comparison purposes.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class BruteForceCollisionGenerator extends AbstractCollisionGenerator {

    /**
     * Creates a new instance with specified hash algorithm
     * 
     * @param hashAlgorithm the hash algorithm
     */
    public BruteForceCollisionGenerator(final HashAlgorithm hashAlgorithm) {
        super(hashAlgorithm);
    }

    @Override
    public List<String> generateCollisions(final int numberOfKeys) {
        throw new UnsupportedOperationException("Operation not implemented");
    }

}
