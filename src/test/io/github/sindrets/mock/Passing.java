package io.github.sindrets.mock;

import io.github.sindrets.fling.TestGroup;
import io.github.sindrets.fling.TestInitiator;

public class Passing {

    @TestGroup
    public void testSomeThings(TestInitiator suite) {
        suite.it("'Lorem' is equal to 'Lorem'").expect("Lorem").toBe("Lorem");
        suite.it("'ipsum' is not equal to 'dolor'").expect("ipsum").not().toBe("dolor");
    }

    @TestGroup(description = "Class property tests")
    public void testSomeClassProperties(TestInitiator suite) {
        suite.it("getCanonicalName() returns correct name").expect(this.getClass().getCanonicalName())
                .toBe("io.github.sindrets.mock.Passing");
        System.out.println("Foo bar baz");
        suite.it("should have 1 declared constructor").expect(this.getClass().getDeclaredConstructors().length).toBe(1);
    }
}