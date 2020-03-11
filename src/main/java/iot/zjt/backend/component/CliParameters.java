package iot.zjt.backend.component;

import java.util.Arrays;

import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.core.cli.Option;

/**
 * For parsing the CLI parameters.
 * 
 * @author Mr Dk.
 * @version 2020/03/11
 */
public class CliParameters {

    private static CommandLine cmd;

    public static CommandLine getCli() {
        return cmd;
    }

    public static void init(String[] args) {
        CLI cli = CLI.create("Vert.x-Java back-end")
            .addOption(new Option()
                .setLongName("help")
                .setShortName("H")
                .setDescription("helper")
                .setHelp(true)
            )
            .addArgument(new Argument()
                .setIndex(0)
                .setArgName("back-end config")
                .setDescription("the path to the configuration file")
            ).addArgument(new Argument()
                .setIndex(1)
                .setArgName("logger config")
                .setDescription("the path to the logger configuration file")
            );

        cmd = cli.parse(Arrays.asList(args));
        if (!cmd.isValid() || cmd.isAskingForHelp()) {
            StringBuilder builder = new StringBuilder();
            cli.usage(builder);
            System.out.println(builder.toString());
        }
    }
}