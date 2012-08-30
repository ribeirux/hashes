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
package org.hashes.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hashes.CollisionInjector;
import org.hashes.collision.DJBX31ACollisionGenerator;
import org.hashes.collision.DJBX33ACollisionGenerator;
import org.hashes.collision.DJBX33XCollisionGenerator;
import org.hashes.collision.V8CollisionGenerator;
import org.hashes.config.Configuration;
import org.hashes.config.Configuration.ConfigurationBuilder;

/**
 * Hashes command line interface implementation.
 * 
 * @author ribeirux
 * @version $Revision$
 */
public class HashesCli {

    private static final Log LOG = LogFactory.getLog(HashesCli.class);

    private static final int SECOND_MILLIS = 1000;

    private static final int FORMATTER_WIDTH = 120;

    private static final String CLI_SYNTAX = "hashes [options...] <POST url>";

    /**
     * Main entry.
     * 
     * @param args command line parameters
     */
    public static void main(final String[] args) {

        final CommandLineParser parser = new GnuParser();
        final Options options = CliOption.buildOptions();

        try {
            final CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(CliOption.HELP.getOption().getOpt())) {
                printHelp(options);
            } else {
                final HashesCli app = new HashesCli();
                app.createCollisionInjector(buidConfiguration(cmd)).start();
            }
        } catch (final Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e.getMessage(), e);
            }

            printHelp(options);
            System.exit(1);
        }
    }

    protected CollisionInjector createCollisionInjector(final Configuration configuration) {
        return new CollisionInjector(configuration);
    }

    private static Configuration buidConfiguration(final CommandLine cmd) throws MalformedURLException, ParseException {

        @SuppressWarnings("unchecked")
        final List<String> arguments = cmd.getArgList();

        if ((arguments == null) || arguments.isEmpty()) {
            throw new ParseException("Missing required option: url");
        } else if (arguments.size() > 1) {
            LOG.warn("Multiple urls are not allowed. Using: " + arguments.get(0));
        }

        final URL url = new URL(arguments.get(0));

        // required fields
        final ConfigurationBuilder builder = new ConfigurationBuilder(url.getHost());

        // optional fields

        // URL
        builder.withSchemeName(url.getProtocol());
        final int port = url.getPort();
        if (port != -1) {
            builder.withPort(port);
        }
        final String path = url.getPath();
        if (!path.isEmpty()) {
            builder.withPath(path);
        }

        // language
        if (cmd.hasOption(CliOption.JAVA.getOption().getOpt())) {
            builder.withCollisionGenerator(new DJBX31ACollisionGenerator());
        } else if (cmd.hasOption(CliOption.PHP.getOption().getOpt())) {
            builder.withCollisionGenerator(new DJBX33ACollisionGenerator());
        } else if (cmd.hasOption(CliOption.ASP.getOption().getOpt())) {
            final String seed = (String) cmd.getParsedOptionValue(CliOption.ASP.getOption().getOpt());
            builder.withCollisionGenerator(new DJBX33XCollisionGenerator(seed));
        } else if (cmd.hasOption(CliOption.V8.getOption().getOpt())) {
            final String seed = (String) cmd.getParsedOptionValue(CliOption.V8.getOption().getOpt());
            builder.withCollisionGenerator(new V8CollisionGenerator(seed));
        }

        final String saveKeys = (String) cmd.getParsedOptionValue(CliOption.SAVE_KEYS.getOption().getOpt());
        if (saveKeys != null) {
            builder.saveCollisionsToFile(new File(saveKeys));
        }

        if (cmd.hasOption(CliOption.WAIT.getOption().getOpt())) {
            builder.waitForResponse();
        }

        if (cmd.hasOption(CliOption.NEW.getOption().getOpt())) {
            builder.generateNewKeys();
        }

        if (cmd.hasOption(CliOption.KEYS.getOption().getOpt())) {
            final Object keys = cmd.getParsedOptionValue(CliOption.KEYS.getOption().getOpt());
            final int numberOfkeys = ((Number) keys).intValue();
            if (numberOfkeys <= 0) {
                throw new ParseException("The number of keys should be greater than 0");
            }
            builder.withNumberOfKeys(numberOfkeys);
        }

        if (cmd.hasOption(CliOption.REQUESTS.getOption().getOpt())) {
            final Object requests = cmd.getParsedOptionValue(CliOption.REQUESTS.getOption().getOpt());
            final int numberOfRequests = ((Number) requests).intValue();
            if (numberOfRequests <= 0) {
                throw new ParseException("The number of requests should be greater than 0");
            }
            builder.withRequestsPerClient(numberOfRequests);
        }

        if (cmd.hasOption(CliOption.CLIENTS.getOption().getOpt())) {
            final Object clients = cmd.getParsedOptionValue(CliOption.CLIENTS.getOption().getOpt());
            final int numberOfClients = ((Number) clients).intValue();
            if (numberOfClients <= 0) {
                throw new ParseException("The number of clients should be greater than 0");
            }
            builder.withNumberOfClients(numberOfClients);
        }

        if (cmd.hasOption(CliOption.CONNECTION_TIMEOUT.getOption().getOpt())) {
            final Object timeout = cmd.getParsedOptionValue(CliOption.CONNECTION_TIMEOUT.getOption().getOpt());
            final int connectionTimeout = ((Number) timeout).intValue();
            if (connectionTimeout < 0) {
                throw new ParseException("The connection timeout should be greater than or equal to 0");
            }
            builder.withConnectTimeout(connectionTimeout * SECOND_MILLIS);
        }

        if (cmd.hasOption(CliOption.READ_TIMEOUT.getOption().getOpt())) {
            final Object timeout = cmd.getParsedOptionValue(CliOption.READ_TIMEOUT.getOption().getOpt());
            final int readTimeout = ((Number) timeout).intValue();
            if (readTimeout < 0) {
                throw new ParseException("The read timeout should be greater than or equal to 0");
            }
            builder.withReadTimeout(readTimeout * SECOND_MILLIS);
        }

        return builder.build();
    }

    private static void printHelp(final Options options) {

        final HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(FORMATTER_WIDTH);

        formatter.printHelp(CLI_SYNTAX, options, false);
    }

}
