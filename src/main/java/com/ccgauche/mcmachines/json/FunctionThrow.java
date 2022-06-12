package com.ccgauche.mcmachines.json;

public interface FunctionThrow<T, E> {
	E apply(T t) throws Exception;
}
