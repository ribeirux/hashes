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
package org.hashes.progress;

/**
 * Factory used to create new progress monitors.
 * <p>
 * The progress monitors cannot be reused after the task is completed and a new progress monitor should be create
 * through a {@link ProgressMonitorFactory} when new task is started.
 * <p>
 * This factory also allows that the code using the monitors doesn't have to be aware of whether they are text based or
 * GUI based or anything else.
 * 
 * @author ribeirux
 */
public interface ProgressMonitorFactory {

    /**
     * Creates a new progress monitor.
     * 
     * @param info name or description of the task
     * @param totalWork the total number of work units or null to indicate the progress in a way witch doesn't require
     *            the total number of units in advance.
     * @return a new progress monitor
     */
    ProgressMonitor createProgressMonitor(String info, Integer totalWork);

}
