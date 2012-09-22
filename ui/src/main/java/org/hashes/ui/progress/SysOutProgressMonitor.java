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
package org.hashes.ui.progress;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.hashes.progress.ProgressMonitor;

import com.google.common.base.Preconditions;

/**
 * Progress monitor which prints the progress of a task to system out.
 * 
 * @author ribeirux
 */
public class SysOutProgressMonitor implements ProgressMonitor {

    private static final int BLOCK_WEIGHT = 4;

    private final String info;

    private final Integer totalWork;

    private boolean done;

    // concurrency
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock read = this.lock.readLock();

    private final Lock write = this.lock.writeLock();

    /**
     * Creates a new immutable instance.
     * 
     * @param info name or description of the task
     * @param totalWork the total number of work units or null to indicate the progress in a way witch doesn't require
     *            the total number of units in advance.
     */
    public SysOutProgressMonitor(final String info, final Integer totalWork) {
        this.info = Preconditions.checkNotNull(info, "info");
        this.totalWork = totalWork == null ? null : totalWork > 0 ? totalWork : 0;
        this.done = false;
        this.printProgress(0);
    }

    @Override
    public void update(final int work) {
        if (this.totalWork != null) {
            if (work * 100 % this.totalWork == 0) {
                final String progress = this.buildProgress(work * 100 / this.totalWork);
                // concurrent reads
                this.read.lock();
                try {
                    if (!this.done) {
                        this.printProgress(progress);
                    }
                } finally {
                    this.read.unlock();
                }
            }
        }
    }

    @Override
    public void done() {
        // if this method is called several times, don't screw the layout
        boolean doneBefore = false;

        this.write.lock();
        try {
            doneBefore = this.done;
            if (!this.done) {
                this.done = true;
            }
        } finally {
            this.write.unlock();
        }

        if (!doneBefore) {
            this.printProgress(100);
            System.out.println();
        }
    }

    private void printProgress(final int percetage) {
        this.printProgress(this.buildProgress(percetage));
    }

    private String buildProgress(final int percentage) {
        final StringBuilder builder = new StringBuilder(this.info + " |");

        for (int i = 0; i < 100 / BLOCK_WEIGHT; i++) {
            if (i < percentage / BLOCK_WEIGHT) {
                builder.append('#');
            } else {
                builder.append(' ');
            }
        }

        return builder.append("| " + percentage + "%\r").toString();
    }

    private void printProgress(final String progress) {
        System.out.print(progress);
    }
}
