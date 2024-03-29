package com.ccgauche.mcmachines.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.json.recipe.MCCraft;
import com.google.common.collect.ImmutableMap;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class CraftingTableCraftProvider implements ICraftingMachine {

	private final List<MCCraft> list = new ArrayList<>();

	public void register(Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2) {
		list.forEach(e -> e.register(map2));
	}

	@Override
	public Class<? extends IRecipe> getCraftModelType() {
		return MCCraft.class;
	}

	@Override
	public void bindCraftModel(@NotNull IRecipe recipe) {
		if (recipe instanceof MCCraft craft) {
			list.add(craft);
		}
	}

	@Override
	public String getCraftModelRegistryKey() {
		return "minecraft:crafting_table";
	}
}
