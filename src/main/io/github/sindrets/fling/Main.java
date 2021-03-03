package io.github.sindrets.fling;

public class Main {

    public static boolean debugMode = false;

    public static void main(String[] args) {
        ArgObject argo = ArgParser.parseArgs(args);

        String envDebug = System.getenv("DEBUG");
        Main.debugMode = envDebug != null && envDebug.equals("1");

        Logger.debug("flags:", (Object) argo.flags.entrySet().toArray());
        Logger.debug("params:", argo.params);

        if (argo.params.size() < 1) {
            System.err.println("No classpaths specified!");
            System.exit(1);
        }

        TestRunner testRunner = new TestRunner(new FlingSpec() {
            {
                try {
                    this.classpaths = argo.params.get(0).split(":|;");
                    this.includes = argo.flags.containsKey("include")
                            ? argo.flags.get("include").split(":|;")
                            : new String[0];
                    this.excludes = argo.flags.containsKey("exclude")
                            ? argo.flags.get("exclude").split(":|;")
                            : new String[0];
                    this.logLevel = Integer.parseInt(argo.flags.getOrDefault("loglevel",
                            Integer.toString(LogLevel.ALL)));
                } catch (NumberFormatException e) {
                    this.logLevel = LogLevel.ALL;
                }
            }
        });

        try {
            testRunner.run();
        } catch (TestFailedException e) {
            System.exit(1);
        }
    }
}
