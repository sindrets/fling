package io.github.sindrets.fling;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgParser {

    public static boolean isFlag(String param) {
        final Pattern flagRegex = Pattern.compile(
                "((?<=^--)[a-zA-Z][a-zA-Z\\d\\-]+$)|"
                        + "((?<=^-)[a-zA-Z]+$)|"
                        + "((?<=^--)([a-zA-Z][a-zA-Z\\d\\-]+)(?:=)(.*)$)");
        final Matcher matcher = flagRegex.matcher(param);
        return matcher.find();
    }

    public static ArgObject parseArgs(String[] args) {
        // Use LinkedHashMap to preserve order
        final LinkedHashMap<String, String> flags = new LinkedHashMap<>();
        final ArrayList<String> params = new ArrayList<>();
        final Pattern letterFlag = Pattern.compile("(?<=^-)[a-zA-Z]+$");
        final Pattern wordFlag = Pattern.compile("(?<=^--)[a-zA-Z][a-zA-Z\\d\\-]+$");
        final Pattern wordValueFlag = Pattern
                .compile("(?<=^--)([a-zA-Z][a-zA-Z\\d\\-]+)(?:=)(.*)$");
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
                flags.put(matcher.group(0), "true");
                continue;
            }

            matcher = wordValueFlag.matcher(param);
            if (matcher.find()) {
                flags.put(matcher.group(1), matcher.group(2));
                continue;
            }

            params.add(param);
        }

        return new ArgObject(flags, params);
    }
}
