package no.stide.fling.expector;

import no.stide.fling.Procedure;

public interface TestExpector {
    public CompareExpector expect(Object actual);
    public ProcedureExpector expect(Procedure fn);
}