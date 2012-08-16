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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Meet in the middle hash collision generator.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public abstract class AbstractMITMCollisionGenerator extends AbstractCollisionGenerator {

    private static final int DICTIONARY_SIZE = (int) Math.pow(2, 18);

    private static final int KEY_SIZE = 7;

    // inclusive
    private static final char START_KEY = ' ';

    // inclusive
    private static final char END_KEY = '~';

    private final String seed;

    /**
     * Creates a new instance with specified seed.
     * 
     * @param seed MITM seed
     */
    public AbstractMITMCollisionGenerator(final String seed) {
        this.seed = Preconditions.checkNotNull(seed, "seed");
    }

    @Override
    public List<String> generateCollisions(final int numberOfKeys) {
        Preconditions.checkArgument(numberOfKeys > 0, "numberOfKeys");

        final int hash = this.hashForth(this.seed);

        final Map<Integer, String> dictionary = this.createDictionary(hash);

        final List<Callable<List<String>>> tasks = this.buildTasks(dictionary, numberOfKeys);

        final ExecutorService executor = Executors.newFixedThreadPool(tasks.size());

        try {
            List<Future<List<String>>> results = Collections.emptyList();

            try {
                results = executor.invokeAll(tasks);
            } finally {
                executor.shutdown();
            }

            Builder<String> collisions = ImmutableList.builder();
            for (final Future<List<String>> future : results) {
                collisions.addAll(future.get());
            }

            return collisions.build();
        } catch (final Exception e) {
            throw new ComputationException(e);
        }
    }

    private Map<Integer, String> createDictionary(final int hash) {
        final Map<Integer, String> dictionary = new HashMap<Integer, String>(DICTIONARY_SIZE);

        for (int i = 0; i < DICTIONARY_SIZE; i++) {
            final String sufix = this.randomString(3);
            dictionary.put(this.hashBack(sufix, hash), sufix);
        }

        return Collections.unmodifiableMap(dictionary);
    }

    private String randomString(final int size) {
        final StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append((char) (Math.random() * (END_KEY - START_KEY + 1) + START_KEY));
        }

        return builder.toString();
    }

    private List<Callable<List<String>>> buildTasks(final Map<Integer, String> dictionary, final int size) {

        final int range = END_KEY - START_KEY + 1;
        final int maxWorkers = Math.min(range, Runtime.getRuntime().availableProcessors());
        final int interval = range / maxWorkers;

        final AtomicLong keyCounter = new AtomicLong();

        Builder<Callable<List<String>>> tasks = ImmutableList.builder();

        for (int i = 0; i < maxWorkers; i++) {
            final char start = (char) (i * interval + START_KEY);
            final char end = (i == maxWorkers - 1 ? END_KEY : (char) (start + interval - 1));

            tasks.add(new MITMWorker(start, end, keyCounter, size, dictionary));
        }

        return tasks.build();
    }

    /**
     * Meet in the middle hash collision worker.
     * 
     * @author ribeirux
     * @version $Revision$
     */
    private class MITMWorker implements Callable<List<String>> {

        private final char start;

        private final char end;

        private final AtomicLong keyCounter;

        private final long maxNumberOfKeys;

        private final Map<Integer, String> dictionary;

        public MITMWorker(final char start, final char end, final AtomicLong keyCounter, final int maxNumberOfKeys,
                final Map<Integer, String> dictionary) {
            this.start = start;
            this.end = end;
            this.keyCounter = keyCounter;
            this.maxNumberOfKeys = maxNumberOfKeys;
            this.dictionary = dictionary;
        }

        @Override
        public List<String> call() {
            final List<String> collisions = new LinkedList<String>();
            this.crack("", this.start, this.end, collisions);

            return collisions;
        }

        private void crack(final String prefix, final char startChar, final char endChar, final List<String> collisions) {

            if (prefix.length() == KEY_SIZE) {
                final String precomp = this.dictionary.get(AbstractMITMCollisionGenerator.this.hashForth(prefix));
                if (precomp != null) {
                    final long currentValue = this.keyCounter.getAndIncrement();
                    if (currentValue < this.maxNumberOfKeys) {
                        collisions.add(prefix + precomp);
                    }
                }
            } else {
                for (char i = startChar; (i <= endChar) && (this.maxNumberOfKeys > this.keyCounter.get()); i++) {
                    this.crack(prefix + i, START_KEY, END_KEY, collisions);
                }
            }
        }
    }

    /**
     * Compute the hash code of the key. This method must be thread safe.
     * 
     * @param key the key to hash
     * @return the hash code
     */
    protected abstract int hashForth(final String key);

    /**
     * Compute the hash back code from the key.
     * 
     * @param key the key to hash back
     * @param hash seed hash code
     * @return the hash back code
     */
    protected abstract int hashBack(final String key, final int hash);
}
