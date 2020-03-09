package no.stide.fling.expector;

public interface ProcedureExpector {
    public ProcedureExpector not();
    public boolean toThrow(Class<? extends Throwable> expectation);
}