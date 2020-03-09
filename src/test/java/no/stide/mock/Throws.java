package no.stide.mock;

import no.stide.fling.TestGroup;
import no.stide.fling.TestInitiator;

public class Throws {

    @TestGroup(description = "Test throw assertion")
    public void throwTest(TestInitiator suite) {
        
        suite.it("should throw RuntimeException").expect(() -> {
            throw new RuntimeException();
        }).toThrow(Exception.class);

        suite.it("should not throw").expect(() -> {
            return;
        }).not().toThrow(Exception.class);

        suite.it("should not throw RuntimeException").expect(() -> {
            throw new RuntimeException();
        }).not().toThrow(RuntimeException.class);
    }
}