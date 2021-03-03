package io.github.sindrets.mock;

import io.github.sindrets.fling.TestGroup;
import io.github.sindrets.fling.TestInitiator;

public class Failing {

    @TestGroup(description = "Testing some things")
    public void testThings(TestInitiator suite) {
        suite.it("42 equals 42").expect(42).toBe(43);
        suite.it("true is true").expect(true).toBe(true);
        suite.it("9 does not equal 10").expect(9).not().toBe(9);
    }
}