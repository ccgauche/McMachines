package com.ccgauche.mcmachines.json;

import org.jetbrains.annotations.NotNull;

public class Dual<T, E> {

    @NotNull
    private final T first;
    @NotNull
    private final E second;


    public Dual(@NotNull T first, @NotNull E second) {
        this.first = first;
        this.second = second;
    }

    public @NotNull T getFirst() {
        return first;
    }

    public @NotNull E getSecond() {
        return second;
    }
}
