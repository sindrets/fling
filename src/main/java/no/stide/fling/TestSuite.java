package no.stide.fling;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import no.stide.jchalk.Chalk;

public class TestSuite {
    protected static int totalTestCount = 0;
    protected static int totalPassCount = 0;
    private final static Chalk chalk = new Chalk();
    private int suitePassCount = 0;
    private String classPath;
    private String description;
    private TestFunction testFn;
    private ArrayList<TestCase> tests = new ArrayList<>();

    public TestSuite(String description, TestFunction fn) {
        this.description = description;
        this.testFn = fn;
    }

    public TestSuite(String classPath, String description, TestFunction fn) {
        this.classPath = classPath;
        this.description = description;
        this.testFn = fn;
    }

    public void run() {
        System.out.println(this.description);

        ByteArrayOutputStream out = Util.pushOutStack();
        this.testFn.invoke(new TestInitiator(this));
        Util.popOutStack();

        for (TestCase test : this.tests) {
            System.out.println(Util.indent(test.toString(), 2));
            if (test.getStatus() == TestStatus.PASSED) {
                this.suitePassCount++;
            }
        }

        if (out.size() > 0) {
            System.out.println("  " + chalk.bgCyan().black().apply(" STD OUT: "));
            System.out.print(Util.indent(out.toString(), 4));
        }

        this.printSummary();
    }

    public static void resetTestCounters() {
        TestSuite.totalTestCount = 0;
        TestSuite.totalPassCount = 0;
    }

    public void printSummary() {
        String s = this.hasPassedTests() ? chalk.green().apply("✓") : chalk.red().apply("✗");
        System.out.println(
            "\n  " + s + " " + chalk.blackBright().apply("Suite summary: ") +
            "Passed " + this.suitePassCount + " out of " + this.tests.size() + " tests."
        );
    }

    public static void printFullSummary() {
        String s = hasPassedAllTests() ? chalk.green().apply("✓") : chalk.red().apply("✗");
        System.out.println(
            "\n" + s + " " + chalk.bold().underline().apply(
                "Full test summary: Passed " + TestSuite.totalPassCount + " out of " + TestSuite.totalTestCount + " tests."
            ) + "\n"
        );
    }

    protected void addTest(TestCase test) {
        this.tests.add(test);
    }

    public static int getTotalTestCount() {
        return TestSuite.totalTestCount;
    }

    public static int getTotalPassCount() {
        return TestSuite.totalPassCount;
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

    public static boolean hasPassedAllTests() {
        return TestSuite.totalPassCount == TestSuite.totalTestCount;
    }

    public String getClassPath() {
        return this.classPath;
    }

    public boolean hasPassedTests() {
        return this.suitePassCount == this.tests.size();
    }

    @Override
    public String toString() {
        String s = chalk.blue().bold().apply(classPath) + "\n"
            + "  " + description + "\n";
        for (TestCase test : this.tests) {
            s += Util.indent(test.toString(), 4) + "\n";
        }
        return s;
    }

    public String toString(TestStatus filter) {
        String s = chalk.blue().bold().apply(classPath) + "\n"
            + "  " + description + "\n";
        for (TestCase test : getTests(filter)) {
            s += Util.indent(test.toString(), 4) + "\n";
        }
        return s;
    }
}
