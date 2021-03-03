package io.github.sindrets.mock;

import io.github.sindrets.fling.TestGroup;
import io.github.sindrets.fling.TestInitiator;

public class UnexpectedEx {

    private int foo() {
        throw new RuntimeException();
    }

    @TestGroup(description = "Unexpected exception should cause test group to fail")
    public void testUnexpected(TestInitiator suite) {
        suite.it("foo").expect(1).toBe(1);
        suite.it("bar").expect(false).toBe(false);
        foo();
    }

    @TestGroup(description = "Too many paramters")
    public void testManyParameters(TestInitiator suite, int a) {}

    @TestGroup(description = "Too few paramters")
    public void testFewParameters() {}
}