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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * DJBX33A hash collision algorithm.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class DJBX31ACollisionAlgorithm extends AbstractEquivalentSubstringsAlgorithm {

    private static final String PRE_BUILT_FILE_NAME = "java.txt";

    @Override
    public int hash(final String key) {
        Preconditions.checkNotNull(key, "key");

        return key.hashCode();
    }

    @Override
    protected List<String> buildSeed() {
        return ImmutableList.of("xw", "yX", "z9");
    }

    @Override
    protected String getPreBuiltCollisionsFileName() {
        return PRE_BUILT_FILE_NAME;
    }
}
