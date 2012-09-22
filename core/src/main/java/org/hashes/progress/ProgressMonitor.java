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
 * This interface should be implemented by objects that monitor the progress of a task. When the task is eventually
 * completed, the monitor cannot be reused.
 * <p>
 * All implementations should be thread safe.
 * 
 * @author ribeirux
 */
public interface ProgressMonitor {

    /**
     * Updates the progress of the task.
     * <p>
     * The progress can be updated from several threads, so the incremental work is not assured.
     * 
     * @param work a non negative number of work units just completed.
     */
    void update(int work);

    /**
     * Notifies that the work is done.
     */
    void done();

}
