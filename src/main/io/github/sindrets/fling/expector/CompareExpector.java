package io.github.sindrets.fling.expector;

public interface CompareExpector {
    public CompareExpector not();
    public boolean toBe(Object expectation);
}