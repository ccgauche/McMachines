package com.ccgauche.mcmachines.json;

import org.jetbrains.annotations.NotNull;

public record Dual<T, E> (@NotNull T first, @NotNull E second) {

	public Dual(@NotNull T first, @NotNull E second) {
		this.first = first;
		this.second = second;
	}
}
