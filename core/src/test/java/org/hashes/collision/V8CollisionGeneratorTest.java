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

import org.testng.annotations.Test;

/**
 * V8 hash collision generator tests.
 * 
 * @author ribeirux
 * @version $Revision$
 */
@Test(groups = "functional", testName = "collision.V8CollisionGeneratorTest")
public class V8CollisionGeneratorTest extends CollisionGeneratorTestBase {

    private static final String SEED = "hashes";

    /**
     * Test V8 hash collision algorithm.
     */
    public void testV8() {
        this.testCollisionGenerator(new V8CollisionGenerator(SEED), true);
    }

    /**
     * Test V8 pre-built hash collisions.
     */
    public void testPreBuiltV8() {
        this.testCollisionGenerator(new V8CollisionGenerator(SEED), false);
    }
}
