package io.github.sindrets.fling;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.github.sindrets.jchalk.Chalk;

public class TestSuite {
    private final static Chalk chalk = new Chalk();
    private int suitePassCount = 0;
    private String classPath;
    private String description;
    private TestFunction testFn;
    private ArrayList<TestCase> tests = new ArrayList<>();
    private ExitStatus exitStatus = ExitStatus.EXIT_SUCCESS;
    private String testStdout = "";
    private int logLevel = LogLevel.ALL;

    public TestSuite(String description, TestFunction fn) {
        this.description = description;
        this.testFn = fn;
    }

    public TestSuite(String classPath, String description, TestFunction fn) {
        this.classPath = classPath;
        this.description = description;
        this.testFn = fn;
    }

    protected TestSuite(String classPath, String description) {
        this.classPath = classPath;
        this.description = description;
    }

    public void run() {
        ByteArrayOutputStream out = Utils.pushOutStack();
        this.testFn.invoke(new TestInitiator(this));
        Utils.popOutStack();
        this.testStdout = out.toString();

        for (TestCase test : this.tests) {
            if (test.getStatus() == TestStatus.PASSED) {
                this.suitePassCount++;
            }
        }

        System.out.println(this.formatSuite());
    }

    public String formatTestStdout() {
        if (this.testStdout.length() > 0
                && (logLevel >= LogLevel.TEST_STDOUT || !this.hasPassedTests())) {

            StringBuilder sb = new StringBuilder();
            sb.append(chalk.bgCyan().black().apply(" OUT: ") + "\n");
            sb.append(Utils.indent(this.testStdout, 2));

            return sb.toString();
        }

        return "";
    }

    public String formatSummary() {
        if (logLevel < LogLevel.INFO) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(this.hasPassedTests() ? chalk.green().apply("✓") : chalk.red().apply("✗"));
        sb.append(" " + chalk.blackBright().apply("Suite summary: ") + "Passed "
                + this.suitePassCount + " out of " + this.tests.size() + " tests.");

        return sb.toString();
    }

    public String formatSuite() {
        StringBuilder sb = new StringBuilder();

        if (logLevel >= LogLevel.TEST_STATUS) {
            sb.append(this.hasPassedTests()
                    ? chalk.bgGreen().black().bold().apply(" PASS ")
                    : chalk.bgRed().black().bold().apply(" FAIL "));
            sb.append(" " + this.description + "\n");

            for (TestCase test : this.tests) {
                sb.append(Utils.indent(test.toString(), 2) + "\n");
            }

            sb.append(Utils.indent(this.formatTestStdout(), 2));
            sb.append(Utils.indent(this.formatSummary(), 2));
            sb.append("\n");

        } else if (logLevel >= LogLevel.INFO) {
            sb.append(this.hasPassedTests()
                    ? chalk.bgGreen().black().bold().apply(" PASS ")
                    : chalk.bgRed().black().bold().apply(" FAIL "));
            sb.append(" " + chalk.blackBright().apply(this.description));

            String testOut = this.formatTestStdout();
            if (!testOut.isEmpty()) {
                sb.append("\n" + Utils.indent(testOut, 2));
            }
        }

        return sb.toString();
    }

    protected void addTest(TestCase test) {
        this.tests.add(test);
    }

    protected void setTestFn(TestFunction testFn) {
        this.testFn = testFn;
    }

    public int getTestCount() {
        return tests.size();
    }

    public int getTestPassCount() {
        return this.suitePassCount;
    }

    public ArrayList<TestCase> getTests() {
        return this.tests;
    }

    public ArrayList<TestCase> getTests(TestStatus filter) {
        ArrayList<TestCase> testList = new ArrayList<>();

        for (TestCase test : this.tests) {
            if (test.getStatus() == filter) {
                testList.add(test);
            }
        }

        return testList;
    }

    public String getDescription() {
        return this.description;
    }

    public String getClassPath() {
        return this.classPath;
    }

    public boolean hasPassedTests() {
        return this.exitStatus == ExitStatus.EXIT_SUCCESS && suitePassCount == this.tests.size();
    }

    public ExitStatus getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(ExitStatus exitStatus) {
        this.exitStatus = exitStatus;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String toString() {
        String s = chalk.blue().bold().apply(classPath) + "\n"
                + "  " + description + "\n";

        for (TestCase test : this.tests) {
            s += Utils.indent(test.toString(), 4) + "\n";
        }

        return s;
    }

    public String toString(TestStatus filter) {
        String s = chalk.blue().bold().apply(classPath) + "\n"
                + "  " + description + "\n";

        if (exitStatus != ExitStatus.EXIT_SUCCESS) {
            s += Utils.indent(chalk.red().apply("Exit code: " + exitStatus.ordinal()), 4) + "\n";
        }

        for (TestCase test : getTests(filter)) {
            s += Utils.indent(test.toString(), 4) + "\n";
        }

        return s;
    }
}
