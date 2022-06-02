package com.ccgauche.mcmachines.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;

public record JSONContext(@Nullable JsonElement object, @NotNull String jsonFile) {

	public JSONContext swap(@NotNull JsonElement object) {
		return new JSONContext(object, jsonFile);
	}

	@Override
	public String toString() {
		return "JSONContext{" + "object=" + object + ", jsonFile='" + jsonFile + '\'' + '}';
	}
}
