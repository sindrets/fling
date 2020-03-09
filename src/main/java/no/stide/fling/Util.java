package no.stide.fling;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class Util {

    private static ArrayList<PrintStream> outStreamStack = new ArrayList<>();
    
    public static String getStackTrace() {
        StringWriter sw = new StringWriter();
        new Throwable().printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String indent(String s, int n) {
        String[] lines = s.split("\\n|\\r|(\\r\\n)", -1);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(new String(new char[n]).replace("\0", " ") + line + "\n");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static ByteArrayOutputStream pushOutStack() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        outStreamStack.add(System.out);
        System.setOut(new PrintStream(baos));
        return baos;
    }

    public static void popOutStack() {
        System.setOut(outStreamStack.remove(outStreamStack.size() - 1));
    }
}