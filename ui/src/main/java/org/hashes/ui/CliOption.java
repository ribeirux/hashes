/*******************************************************************************
 *
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
 *    
 *******************************************************************************/
package org.hashes.ui;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 * Available command line options.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public enum CliOption {

    /**
     * Help
     */
    @SuppressWarnings("static-access")
    HELP(OptionBuilder.withDescription("Print this message").withLongOpt("help").create("h")),
    /**
     * PHP
     */
    @SuppressWarnings("static-access")
    PHP(OptionBuilder.withDescription("Build PHP payload (default ON)").withLongOpt("php").create("p")),
    /**
     * JAVA
     */
    @SuppressWarnings("static-access")
    JAVA(OptionBuilder.withDescription("Build JAVA payload (default OFF)").withLongOpt("java").create("j")),
    /**
     * ASP
     */
    @SuppressWarnings("static-access")
    ASP(OptionBuilder.withDescription("Build ASP payload (default OFF)").hasOptionalArg().withArgName("seed")
            .withType(String.class).withLongOpt("asp").create("a")),
    /**
     * V8
     */
    @SuppressWarnings("static-access")
    V8(OptionBuilder.withDescription("Build V8 payload (default OFF)").hasOptionalArg().withArgName("seed")
            .withType(String.class).withLongOpt("v8").create("g")),
    /**
     * Save keys
     */
    @SuppressWarnings("static-access")
    SAVE_KEYS(OptionBuilder.withDescription("Save keys to file (default OFF)").hasArg().withArgName("file")
            .withType(String.class).withLongOpt("save").create("s")),
    /**
     * Wait for response
     */
    @SuppressWarnings("static-access")
    WAIT(OptionBuilder.withDescription("Wait for response (default OFF)").withLongOpt("wait").create("w")),
    /**
     * Generate new keys
     */
    @SuppressWarnings("static-access")
    NEW(OptionBuilder.withDescription("Generate new keys instead of using pre-built collisions (default OFF)")
            .withLongOpt("new").create("n")),
    /**
     * Keys
     */
    @SuppressWarnings("static-access")
    KEYS(OptionBuilder.withDescription("Number of keys to inject per request (default 85000)").hasArg()
            .withArgName("keys").withType(Number.class).withLongOpt("keys").create("k")),
    /**
     * Number of requests per client
     */
    @SuppressWarnings("static-access")
    REQUESTS(OptionBuilder.withDescription("Number of requests to submit per client (default 1)").hasArg()
            .withArgName("requests").withType(Number.class).withLongOpt("requests").create("r")),
    /**
     * Number of clients
     */
    @SuppressWarnings("static-access")
    CLIENTS(OptionBuilder.withDescription("Number of clients to run (default 1)").hasArg().withArgName("clients")
            .withType(Number.class).withLongOpt("clients").create("c")),
    /**
     * Connection timeout in seconds
     */
    @SuppressWarnings("static-access")
    CONNECTION_TIMEOUT(OptionBuilder
            .withDescription("Connection timeout in seconds, zero to disable timeout (default 60)").hasArg()
            .withArgName("timeout").withType(Number.class).withLongOpt("connection-timeout").create("b")),
    /**
     * Read timeout in seconds
     */
    @SuppressWarnings("static-access")
    READ_TIMEOUT(OptionBuilder.withDescription("Read timeout in seconds, zero to disable timeout (default 60)")
            .hasArg().withArgName("timeout").withType(Number.class).withLongOpt("read-timeout").create("d"));

    private final Option option;

    private CliOption(final Option option) {
        this.option = option;
    }

    /**
     * Gets the option.
     * 
     * @return the option
     */
    public Option getOption() {
        return option;
    }

    /**
     * Build all command line options.
     * 
     * @return command line options
     */
    public static Options buildOptions() {

        final OptionGroup lang = new OptionGroup();
        lang.setRequired(false);
        lang.addOption(PHP.getOption());
        lang.addOption(JAVA.getOption());
        lang.addOption(ASP.getOption());
        lang.addOption(V8.getOption());

        final Options options = new Options();
        options.addOption(HELP.getOption());
        options.addOption(SAVE_KEYS.getOption());
        options.addOption(WAIT.getOption());
        options.addOption(NEW.getOption());
        options.addOption(KEYS.getOption());
        options.addOption(REQUESTS.getOption());
        options.addOption(CLIENTS.getOption());
        options.addOption(CONNECTION_TIMEOUT.getOption());
        options.addOption(READ_TIMEOUT.getOption());
        options.addOptionGroup(lang);

        return options;
    }

}
