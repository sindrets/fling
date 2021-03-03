package io.github.sindrets.fling;

@FunctionalInterface
public interface TestFunction {
    public void invoke(TestInitiator suite);
}
