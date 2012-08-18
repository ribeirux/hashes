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
import org.testng.annotations.Test;

/**
 * DJBX31ACollisionGenerator test.
 * 
 * @author ribeirux
 * @version $Revision$
 */
@Test(groups = "functional", testName = "collision.DJBX33ACollisionGeneratorTest")
public class DJBX31ACollisionGeneratorTest {

    /**
     * Test DJBX31A hash collision generation.
     */
    public void testCollisions() {
        final int numberOfKeys = 1000;

        final DJBX31ACollisionGenerator gen = new DJBX31ACollisionGenerator();
        final List<String> collisions = gen.generateCollisions(numberOfKeys);

        Assert.assertEquals(collisions.size(), numberOfKeys);

        final int hash = collisions.get(0).hashCode();

        for (final String key : collisions) {
            Assert.assertEquals(key.hashCode(), hash);
        }
    }

}
