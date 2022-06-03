package com.ccgauche.mcmachines.json.recipe;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.json.DParser;
import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.JSONContext;
import com.ccgauche.mcmachines.registry.CraftRegistry;

public interface IRecipe {

	String on();

	static IRecipe parse(JSONContext context)
			throws FileException, NoSuchFieldException, InstantiationException, IllegalAccessException {
		IDProvider on1 = (IDProvider) DParser.parse(IDProvider.class.getTypeName(), context);
		var on = on1.on;
		var k = CraftRegistry.get(on);
		if (k == null) {
			throw new FileException("Can't find craft handler for " + on);
		}
		if (k.getCraftModelType().equals(MCCraft.class)) {
			return (IRecipe) DParser.parse(MCCraft.class.getTypeName(), context);
		} else if (k.getCraftModelType().equals(TransformerCraft.class)) {
			return (IRecipe) DParser.parse(TransformerCraft.class.getTypeName(), context);
		} else if (k.getCraftModelType().equals(GeneratorCraft.class)) {
			return (IRecipe) DParser.parse(GeneratorCraft.class.getTypeName(), context);
		} else {
			throw new FileException("Can't find craftmodel " + k.getCraftModelType().getSimpleName());
		}
	}

	class IDProvider {

		@NotNull
		public String on;
	}
}
