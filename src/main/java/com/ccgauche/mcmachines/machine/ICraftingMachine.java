package com.ccgauche.mcmachines.machine;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.json.recipe.IRecipe;

public interface ICraftingMachine {

	Class<? extends IRecipe> getCraftModelType();

	void bindCraftModel(@NotNull IRecipe recipe);

	String getCraftModelRegistryKey();
}
