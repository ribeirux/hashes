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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * DJBXA hash collision generator.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public abstract class AbstractDJBXACollisionGenerator extends AbstractCollisionGenerator {

    @Override
    public List<String> generateCollisions(final int numberOfKeys) {
        Preconditions.checkArgument(numberOfKeys > 0, "numberOfKeys");

        final List<String> seed = this.buildSeed();

        final List<String> generatedKeys = new ArrayList<String>(numberOfKeys);

        if (numberOfKeys <= seed.size()) {
            generatedKeys.addAll(seed.subList(0, numberOfKeys));
        } else {
            final int iterations = (int) Math.ceil(Math.log(numberOfKeys) / Math.log(seed.size()));
            this.buildCombinations(seed, iterations, "", numberOfKeys, generatedKeys);
        }

        return Collections.unmodifiableList(generatedKeys);
    }

    private void buildCombinations(final List<String> seed, final int iterations, final String combination,
            final int numberOfKeys, final List<String> result) {
        if (iterations == 0) {
            result.add(combination);
        } else {
            for (String entry : seed) {
                if (result.size() < numberOfKeys) {
                    this.buildCombinations(seed, iterations - 1, combination + entry, numberOfKeys, result);
                } else {
                    break;
                }
            }
        }
    }

    protected abstract List<String> buildSeed();
}
