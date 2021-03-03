package io.github.sindrets.fling.expector;

public interface ProcedureExpector {
    public ProcedureExpector not();
    public boolean toThrow(Class<? extends Throwable> expectation);
}