package no.stide.mock;

import no.stide.fling.TestGroup;
import no.stide.fling.TestInitiator;

public class Failing {

    @TestGroup(description = "Testing some things")
    public void testThings(TestInitiator suite) {
        suite.it("42 equals 42").expect(42).toBe(43);
        suite.it("true is true").expect(true).toBe(true);
        suite.it("9 does not equal 10").expect(9).not().toBe(9);
    }
}