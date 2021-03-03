package io.github.sindrets.fling;

@FunctionalInterface
public interface Procedure {
    void invoke() throws Throwable;
}