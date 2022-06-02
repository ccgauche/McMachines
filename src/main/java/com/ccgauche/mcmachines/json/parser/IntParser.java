package com.ccgauche.mcmachines.json.parser;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.JSONContext;

public class IntParser {
	@NotNull
	public static Integer parse(@NotNull JSONContext object) throws FileException {
		if (object.object() == null || !object.object().isJsonPrimitive()) {
			throw new FileException("Invalid JSON file \"" + object.jsonFile() + "\", expected int (" + object + ")");
		}
		return object.object().getAsInt();

	}

}