package no.stide.fling;

import no.stide.jchalk.Chalk;
import no.stide.fling.expector.*;

public class TestCase implements TestExpector, CompareExpector, ProcedureExpector {
    private String description;
    private boolean not = false;
    private Object actual;
    private Object expectation;
    private TestStatus status = TestStatus.NOT_STARTED;
    private boolean omitExplanation = false;
    private String explanationFormat;
    private final Chalk chalk = new Chalk();

    public TestCase(String description) {
        this.description = description;
    }

    public CompareExpector expect(Object actual) {
        this.not = false;
        this.actual = actual;
        return this;
    }

    public ProcedureExpector expect(Procedure fn) {
        this.not = false;
        this.actual = fn;
        return this;
    }

    public TestCase not() {
        this.not = true;
        return this;
    }

    public boolean toBe(Object expectation) {
        this.expectation = expectation;
        this.explanationFormat = "Expected '{{actual}}' {{not}}to be '{{expectation}}'!";
        boolean isEqual = expectation == null || actual == null ? actual == expectation : actual.equals(expectation);
        if (isEqual && !not || !isEqual && not) {
            this.status = TestStatus.PASSED;
            return true;
        }
        this.status = TestStatus.FAILED;
        return false;
    }

    public boolean toThrow(Class<? extends Throwable> expectation) {
        this.expectation = expectation;
        this.explanationFormat = "Expected method {{not}}to throw '{{expectation}}'!";
        try {
            ((Procedure)actual).invoke();
            if (not) {
                this.status = TestStatus.PASSED;
                return true;
            }
        } catch (Throwable e) {
            boolean isEqual = expectation.isInstance(e);
            if (isEqual && !not || !isEqual && not) {
                this.status = TestStatus.PASSED;
                return true;
            }
        }
        this.status = TestStatus.FAILED;
        return false;
    }

    public TestStatus getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        switch (this.status) {
            case NOT_STARTED:
                s.append(
                    chalk.bgYellow().black().bold().apply(" NOT STARTED ") + " " + 
                    chalk.blackBright().apply(this.description)
                );
                break;

            case FAILED:
                s.append(
                    chalk.bgRed().black().bold().apply(" FAIL ") + " " + 
                    chalk.blackBright().apply(this.description)
                );
                if (!omitExplanation) {
                    // s.append(chalk.blackBright().apply(": "));
                    s.append("\n  → ");
                    
                    String explanation = explanationFormat.replaceAll(
                        "\\{\\{actual\\}\\}", 
                        chalk.bold().apply(this.actual)
                    );
                    explanation = explanation.replaceAll(
                        "\\{\\{not\\}\\}", 
                        not ? "not " : ""
                    );
                    explanation = explanation.replaceAll(
                        "\\{\\{expectation\\}\\}", 
                        chalk.bold().apply(expectation)
                    );

                    s.append(explanation);
                }
                break;

            case PASSED:
                s.append(
                    chalk.bgGreen().black().bold().apply(" PASS ") + " " + 
                    chalk.blackBright().apply(this.description)
                );
                break;
        }

        return s.toString();
    }
}
