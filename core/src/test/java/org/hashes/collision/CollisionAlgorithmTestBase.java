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

import org.testng.Assert;

/**
 * Base class for collision algorithm tests.
 * 
 * @author pedroribeiro
 */
public class CollisionAlgorithmTestBase {

    // higher than 0
    private static final int NUMBER_OF_KEYS = 1000;

    /**
     * Test hash collision algorithm.
     * 
     * @param algorithm collision generator algorithm
     * @param forceNew forces the generation of new keys instead of using pre-built
     */
    public void testCollisionGeneratorAlgorithm(final AbstractCollisionAlgorithm algorithm,
            final boolean forceNew) {

        final List<String> collisions = algorithm.generateCollisions(NUMBER_OF_KEYS, forceNew);

        Assert.assertEquals(collisions.size(), NUMBER_OF_KEYS);

        final int hash = algorithm.hash(collisions.get(0));

        for (final String key : collisions) {
            Assert.assertEquals(algorithm.hash(key), hash);
        }
    }

}
