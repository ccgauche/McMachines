package com.ccgauche.mcmachines.json.parser;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.JSONContext;
import com.google.gson.JsonElement;

public class DataCompoundParser {

	@NotNull
	public static DataCompound parse(@NotNull JSONContext object) throws Exception {
		if (object.object() == null || !object.object().isJsonObject()) {
			throw new FileException(
					"Invalid JSON file \"" + object.jsonFile() + "\", invalid DataCompound (" + object + ")");
		}
		var compound = new DataCompound();
		var obj1 = object.object().getAsJsonObject();
		for (Map.Entry<String, JsonElement> el : obj1.entrySet()) {
			try {
				compound.setInt(el.getKey(), Integer.parseInt(el.getValue().getAsString()));
			} catch (NumberFormatException e) {
				compound.setString(el.getKey(), el.getValue().getAsString());
			}

		}
		return compound;
	}
}
