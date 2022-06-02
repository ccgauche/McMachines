package com.ccgauche.mcmachines.json.parser;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.json.Dual;
import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.FunctionThrow;
import com.ccgauche.mcmachines.json.JSONContext;

public class DualParser {

	@NotNull
	public static <T, E> Dual<T, E> parse(@NotNull JSONContext object, @NotNull FunctionThrow<JSONContext, T> func,
			@NotNull FunctionThrow<JSONContext, E> func1)
			throws FileException, NoSuchFieldException, InstantiationException, IllegalAccessException {
		if (object.object() == null || !object.object().isJsonArray())
			throw new FileException("Invalid JSON file \"" + object.jsonFile() + "\", expected array (" + object + ")");

		var j = object.object().getAsJsonArray();
		if (j.size() != 2)
			throw new FileException(
					"Invalid JSON file \"" + object.jsonFile() + "\", expected array of size 2 (" + object + ")");

		return new Dual<>(func.apply(object.swap(j.get(0))), func1.apply(object.swap(j.get(1))));

	}
}
