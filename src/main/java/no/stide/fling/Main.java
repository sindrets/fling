package no.stide.fling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static boolean debugMode = false;
    static LinkedHashMap<String, String> flags;
    static ArrayList<String> params;

    public static boolean isFlag(String param) {
        final Pattern flagRegex = Pattern.compile(
                "((?<=^--)[a-zA-Z][a-zA-Z\\d]+$)|((?<=^-)[a-zA-Z]+$)|((?<=^--)([a-zA-Z][a-zA-Z\\d]+)(?:=)(.*)$)");
        final Matcher matcher = flagRegex.matcher(param);
        return matcher.find();
    }

    public static void parseFlags(String[] args) {
        final LinkedHashMap<String, String> flags = new LinkedHashMap<>();
        final ArrayList<String> params = new ArrayList<>();
        final Pattern letterFlag = Pattern.compile("(?<=^-)[a-zA-Z]+$");
        final Pattern wordFlag = Pattern.compile("(?<=^--)[a-zA-Z][a-zA-Z\\d]+$");
        final Pattern wordValueFlag = Pattern.compile("(?<=^--)([a-zA-Z][a-zA-Z\\d]+)(?:=)(.*)$");
        Matcher matcher;

        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            String nextParam = i < args.length - 1 ? args[i + 1] : null;
            boolean nextIsParam = nextParam != null ? !isFlag(nextParam) : false;

            matcher = letterFlag.matcher(param);
            if (matcher.find()) {
                char[] chars = matcher.group(0).toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    if (j == chars.length - 1 && nextIsParam) {
                        flags.put(String.valueOf(chars[j]), nextParam);
                        i++;
                        break;
                    }
                    flags.put(String.valueOf(chars[j]), "true");
                }
                continue;
            }

            matcher = wordFlag.matcher(param);
            if (matcher.find()) {
                if (nextIsParam) {
                    flags.put(matcher.group(0), nextParam);
                    i++;
                } else {
                    flags.put(matcher.group(0), "true");
                }
                continue;
            }

            matcher = wordValueFlag.matcher(param);
            if (matcher.find()) {
                flags.put(matcher.group(1), matcher.group(2));
                continue;
            }

            params.add(param);
        }

        Main.flags = flags;
        Main.params = params;
    }

    public static void main(String[] args) {
        Main.parseFlags(args);
        Main.debugMode = flags.get("debug") == "true";

        if (debugMode) {
            System.out.println("flags: " + Arrays.toString(flags.entrySet().toArray()));
            System.out.println("params: " + params);
        }

        if (params.size() < 1) {
            System.err.println("No classpaths specified!");
            System.exit(1);
        }

        String[] includes = flags.containsKey("include") ? flags.get("include").split(":|;") : new String[0];
        String[] excludes = flags.containsKey("exclude") ? flags.get("exclude").split(":|;") : new String[0];
        TestRunner testRunner = new TestRunner(params.get(0).split(":|;"));
        testRunner.setIncludes(includes);
        testRunner.setExcludes(excludes);
        try {
            testRunner.run();
        } catch (TestFailedException e) {
            System.exit(1);
        }
    }
}
