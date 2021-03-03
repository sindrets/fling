package io.github.sindrets.fling;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Stack;

public class Utils {
    private static Stack<PrintStream> outStreamStack = new Stack<>();
    private static PrintStream originalErr = System.err;

    public static String indent(String s, int n) {
        if (s.isEmpty()) {
            return "";
        }

        String[] lines = s.split("\\n|\\r|(\\r\\n)", -1);
        StringBuilder sb = new StringBuilder();

        for (String line : lines) {
            sb.append(line.isEmpty()
                    ? "\n"
                    : new String(new char[n]).replace("\0", " ") + line + "\n");
        }

        return sb.substring(0, sb.length() - 1);
    }

    public static ByteArrayOutputStream pushOutStack() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        outStreamStack.push(System.out);
        PrintStream newOut = new PrintStream(baos);
        System.setOut(newOut);
        System.setErr(newOut);
        Logger.setOut(newOut);
        Logger.setErr(newOut);

        return baos;
    }

    public static void popOutStack() {
        if (outStreamStack.empty()) {
            return;
        }

        PrintStream lastOut = outStreamStack.pop();
        System.setOut(lastOut);
        Logger.setOut(lastOut);

        if (outStreamStack.size() == 0) {
            System.setErr(originalErr);
            Logger.setErr(originalErr);
        } else {
            System.setErr(lastOut);
            Logger.setErr(lastOut);
        }
    }
}
