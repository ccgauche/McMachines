package com.ccgauche.mcmachines.json.parser;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.JSONContext;

public class StringParser {
	@NotNull
	public static String parse(@NotNull JSONContext object) throws Exception {
		if (object.object() == null || object.object().getAsString() == null) {
			throw new FileException(
					"Invalid JSON file \"" + object.jsonFile() + "\", expected string (" + object + ")");
		}
		return object.object().getAsString();
	}
}
