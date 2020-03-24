package no.stide;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import no.stide.fling.TestFailedException;
import no.stide.fling.TestRunner;

public class TestRunnerTest {

    @Test public void shouldFail() {
        Assertions.assertThrows(TestFailedException.class, () -> {
            TestRunner testRunner = new TestRunner(new String[] { "build/classes/java/test" });
            testRunner.setIncludes("**/mock/Failing*");
            testRunner.run();
        });
    }

    @Test public void shouldPass() {
        Assertions.assertDoesNotThrow(() -> {
            TestRunner testRunner = new TestRunner(new String[] { "build/classes/java/test" });
            testRunner.setIncludes("**/mock/Passing*");
            testRunner.run();
        });
    }

    @Test public void testUnexpected() {
        TestRunner testRunner = new TestRunner(new String[] { "build/classes/java/test" });
        testRunner.setIncludes("**/mock/UnexpectedEx*");
        Assertions.assertThrows(TestFailedException.class, () -> {
            testRunner.run();
        });
        Assertions.assertEquals(testRunner.getUnitTest().getTotalNonSuccessExits(), 3);
    }
}