package com.ccgauche.mcmachines.json;

import org.jetbrains.annotations.NotNull;

/**
 * A dual object that can be used to store two objects of different types.
 * Similar to a {@link java.util.Map.Entry} or tuples in other languages.
 * 
 * @param <T> The first object type
 * @param <E> The second object type
 */
public record Dual<T, E> (@NotNull T first, @NotNull E second) {

	public Dual(@NotNull T first, @NotNull E second) {
		this.first = first;
		this.second = second;
	}
}
