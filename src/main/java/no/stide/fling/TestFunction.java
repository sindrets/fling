package no.stide.fling;

@FunctionalInterface
public interface TestFunction {
    public void invoke(TestInitiator suite);
}
