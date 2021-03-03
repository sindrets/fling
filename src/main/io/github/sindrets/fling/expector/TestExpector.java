package io.github.sindrets.fling.expector;

import io.github.sindrets.fling.Procedure;

public interface TestExpector {
    public CompareExpector expect(Object actual);
    public ProcedureExpector expect(Procedure fn);
}