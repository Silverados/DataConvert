package com.wyw;

import org.apache.commons.cli.*;

import java.util.List;

public class App {
    public static void main(String[] args) {
        var context = AppContext.getInstance();
        Options options = new Options();
        var client = new Option("c", "client", false, "Convert to predefine client format.");
        var server = new Option("s", "server", false, "Convert to predefine server format.");
        var tag = new Option("t", "tag", true, "Convert only specified tag.");
        var help = new Option("h", "help", false, "Show help.");
        var merge = new Option("m", "merge", false, "Merge source.");
        options.addOption(client).addOption(server).addOption(tag).addOption(help).addOption(merge);

        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLineParser commandLineParser = new DefaultParser();
        try {
            CommandLine parse = commandLineParser.parse(options, args);
            options.getOptions().forEach(option -> {
                if (parse.hasOption(option)) {
//                    System.out.println(option.getOpt() + ":" + (parse.getOptionValue(option) == null ? parse.hasOption(option) : parse.getOptionValue(option)));
                }
            });

            if (parse.hasOption(help)) {
                printHelp(helpFormatter, options);
            }

            context.tag = parse.hasOption(tag) ? parse.getOptionValue(tag).toLowerCase() : Constant.DEFAULT_TAG;
            context.isExport = List.of(parse.hasOption(server), parse.hasOption(client));
            context.mergeSource = parse.hasOption(merge);

            // 未指定是否导出客户端或者服务端: 导全部
            if (!parse.hasOption(client) && !parse.hasOption(server)) {
                context.isExport = List.of(Boolean.TRUE, Boolean.TRUE);
            }

            var converter = new Converter();
            converter.convert();
        } catch (ParseException e) {
            printHelp(helpFormatter, options);
            System.exit(1);
        }
    }

    public static void printHelp(HelpFormatter formatter, Options options) {
        formatter.printHelp("[-c][-s][-t --tag tag]", options);
    }
}
