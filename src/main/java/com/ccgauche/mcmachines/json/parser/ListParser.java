package com.ccgauche.mcmachines.json.parser;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.FunctionThrow;
import com.ccgauche.mcmachines.json.JSONContext;
import com.google.gson.JsonElement;

public class ListParser {

	@NotNull
	public static <T> List<T> parse(@NotNull JSONContext object, @NotNull FunctionThrow<JSONContext, T> func)
			throws FileException, NoSuchFieldException, InstantiationException, IllegalAccessException {
		if (object.object() == null || !object.object().isJsonArray())
			throw new FileException("Invalid JSON file \"" + object.jsonFile() + "\", expected array (" + object + ")");

		List<T> list = new ArrayList<>();
		for (JsonElement e : object.object().getAsJsonArray()) {
			var k = func.apply(object.swap(e));
			list.add(k);
		}
		return list;
	}

	@NotNull
	public static <T> List<T> parseOrPlain(@NotNull JSONContext object, @NotNull FunctionThrow<JSONContext, T> func)
			throws FileException, NoSuchFieldException, InstantiationException, IllegalAccessException {
		if (object.object() != null && object.object().isJsonArray()) {
			return parse(object, func);
		} else {
			return List.of(func.apply(object));
		}
	}
}
