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

import org.hashes.progress.ProgressMonitor;
import org.hashes.progress.ProgressMonitorFactory;

/**
 * Creates a progress monitor which prints the progress of a task to system out.
 * 
 * @author ribeirux
 */
public class SysOutProgressMonitorFactory implements ProgressMonitorFactory {

    @Override
    public ProgressMonitor createProgressMonitor(final String info, final Integer totalWork) {
        return new SysOutProgressMonitor(info, totalWork);
    }

}
