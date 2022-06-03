package com.ccgauche.mcmachines.json.recipe;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.ExampleMod;
import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.conditions.ICondition;
import com.ccgauche.mcmachines.registry.CraftRegistry;
import com.google.common.collect.ImmutableMap;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@SuppressWarnings("NotNullFieldNotInitialized")
public class MCCraft implements IRecipe {

	@NotNull
	public String on;
	@NotNull
	public ItemStack output;
	@NotNull
	public List<@NotNull List<@Nullable CItem>> inputs;
	public Optional<ICondition> conditions;

	public void register(Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2) {
		var recipe = create();
		CraftRegistry.recipes.add(recipe);
		map2.computeIfAbsent(RecipeType.CRAFTING, (_i) -> ImmutableMap.builder()).put(recipe.getId(), recipe);
	}

	public ShapedRecipe create() {
		int height = inputs.size();
		int width = inputs.get(0).size();
		@NotNull
		Ingredient[] items = new Ingredient[height * width];
		int k = 0;
		for (var k1 : inputs) {
			for (var k2 : k1) {
				if (k2 == null) {
					items[k] = Ingredient.EMPTY;
				} else {
					Ingredient i = Ingredient.ofStacks(k2.asStack(1));
					items[k] = i;
				}
				k++;
			}
		}
		ExampleMod.KKK++;
		return new ShapedRecipe(new Identifier("abcdefg", "a" + ExampleMod.KKK), "", width, height,
				DefaultedList.copyOf(Ingredient.EMPTY, items), output);
	}

	@Override
	public String on() {
		return on;
	}
}
