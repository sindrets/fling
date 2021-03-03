package io.github.sindrets.fling;

import java.util.ArrayList;

import io.github.sindrets.jchalk.Chalk;

public class UnitTest {

    private static final Chalk chalk = new Chalk();
    private ArrayList<TestSuite> testSuites = new ArrayList<>();
    private int logLevel = LogLevel.ALL;

    public boolean hasPassedAllTests() {
        for (TestSuite suite : testSuites) {
            if (!suite.hasPassedTests()) {
                return false;
            }
        }

        return true;
    }

    public void addTestSuite(TestSuite testSuite) {
        this.testSuites.add(testSuite);
    }

    public int getTotalTestCount() {
        int total = 0;

        for (TestSuite suite : testSuites) {
            total += suite.getTestCount();
        }

        return total;
    }

    public int getTotalPassCount() {
        int total = 0;

        for (TestSuite suite : testSuites) {
            total += suite.getTestPassCount();
        }

        return total;
    }

    public int getTotalNonSuccessExits() {
        int total = 0;

        for (TestSuite suite : testSuites) {
            if (suite.getExitStatus() != ExitStatus.EXIT_SUCCESS) {
                total++;
            }
        }

        return total;
    }

    public void printFullSummary() {
        if (logLevel < LogLevel.INFO) {
            return;
        }

        int nonSuccessExits = getTotalNonSuccessExits();
        StringBuilder s = new StringBuilder();
        s.append("\n");
        s.append(hasPassedAllTests() ? chalk.green().apply("✓") : chalk.red().apply("✗"));
        s.append(" " + chalk.bold().underline().apply("Full test summary: Passed "
                + getTotalPassCount() + " out of " + getTotalTestCount() + " tests."));

        if (nonSuccessExits > 0) {
            s.append(" " + nonSuccessExits + " test suite");

            if (nonSuccessExits > 1) {
                s.append("s");
            }

            s.append(" had a non successfull exit.");
        }

        System.out.println(s.toString());
    }

    public ArrayList<TestSuite> getTestSuites() {
        return testSuites;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }
}
