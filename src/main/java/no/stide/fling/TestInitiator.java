package no.stide.fling;

import no.stide.fling.expector.TestExpector;

public class TestInitiator {
    private TestSuite parent;

    public TestInitiator(TestSuite parent) {
        this.parent = parent;
    }

    public TestExpector it(String description) {
        TestCase test = new TestCase(description);
        this.parent.addTest(test);
        return test;
    }
}