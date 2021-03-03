package io.github.sindrets;

import io.github.sindrets.fling.FlingSpec;
import io.github.sindrets.fling.TestFailedException;
import io.github.sindrets.fling.TestGroup;
import io.github.sindrets.fling.TestInitiator;
import io.github.sindrets.fling.TestRunner;

public class TestRunnerTest {

    @TestGroup(description = "Ensure that failing tests actually fail")
    public void shouldFail(TestInitiator suite) {
        suite.it("should fail and throw").expect(() -> {
            TestRunner testRunner = new TestRunner(new FlingSpec() {
                {
                    this.classpaths = new String[] { "build/classes/test" };
                    this.includes = new String[] { "**/mock/Failing*" };
                }
            });
            testRunner.run();
        }).toThrow(TestFailedException.class);
    }

    @TestGroup(description = "Ensure that passing tests actually pass")
    public void shouldPass(TestInitiator suite) {
        suite.it("should pass and not throw anything").expect(() -> {
            TestRunner testRunner = new TestRunner(new FlingSpec() {
                {
                    this.classpaths = new String[] { "build/classes/test" };
                    this.includes = new String[] { "**/mock/Passing*" };
                }
            });
            testRunner.run();
        }).not().toThrow(Throwable.class);
    }

    @TestGroup(description = "Ensure that tests fail on unexpected ex")
    public void testUnexpected(TestInitiator suite) {
        TestRunner testRunner = new TestRunner(new FlingSpec() {
            {
                this.classpaths = new String[] { "build/classes/test" };
                this.includes = new String[] { "**/mock/UnexpectedEx*" };
            }
        });

        suite.it("should fail and throw").expect(() -> {
            testRunner.run();
        }).toThrow(TestFailedException.class);

        suite.it("should have 3 unsuccessfull exits")
                .expect(testRunner.getUnitTest().getTotalNonSuccessExits())
                .toBe(3);
    }
}
