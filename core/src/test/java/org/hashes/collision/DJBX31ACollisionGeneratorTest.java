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
 * DJBX31A hash collision generator test.
 * 
 * @author ribeirux
 * @version $Revision$
 */
@Test(groups = "functional", testName = "collision.DJBX31ACollisionGeneratorTest")
public class DJBX31ACollisionGeneratorTest extends CollisionGeneratorTestBase {

    /**
     * Test DJBX31A hash collision algorithm.
     */
    public void testDJBX31A() {
        this.testCollisionGenerator(new DJBX31ACollisionGenerator(), true);
    }

    /**
     * Test DJBX31A pre-built hash collisions.
     */
    public void testPreBuiltDJBX31A() {
        this.testCollisionGenerator(new DJBX31ACollisionGenerator(), false);
    }

}
