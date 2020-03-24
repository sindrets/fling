package no.stide.fling;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import no.stide.jchalk.Chalk;

public class TestSuite {
    private final static Chalk chalk = new Chalk();
    private int suitePassCount = 0;
    private String classPath;
    private String description;
    private TestFunction testFn;
    private ArrayList<TestCase> tests = new ArrayList<>();
    private ExitStatus exitStatus = ExitStatus.EXIT_SUCCESS;

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

    public void printSummary() {
        String s = this.hasPassedTests() ? chalk.green().apply("✓") : chalk.red().apply("✗");
        System.out.println(
            "\n  " + s + " " + chalk.blackBright().apply("Suite summary: ") +
            "Passed " + this.suitePassCount + " out of " + this.tests.size() + " tests."
        );
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
        if (exitStatus != ExitStatus.EXIT_SUCCESS) {
            s += Util.indent(chalk.red().apply("Exit code: " + exitStatus.ordinal()), 4) + "\n";
        }
        for (TestCase test : getTests(filter)) {
            s += Util.indent(test.toString(), 4) + "\n";
        }
        return s;
    }
}
