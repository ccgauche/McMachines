package com.ccgauche.mcmachines.lang.bindings;

import java.util.List;
import java.util.Objects;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.recipe.MCCraft;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;
import com.ccgauche.mcmachines.registry.CraftRegistry;

import net.minecraft.item.ItemStack;

@TypeAlias(name = "ShapedRecipe")
public class ShapedRecipeBinding {

	public static MCCraft $new(ItemStack stack, List<List<CItem>> items) {
		return new MCCraft(stack, items);
	}

	@Method
	public static MCCraft register(MCCraft craft) {
		Objects.requireNonNull(CraftRegistry.get("minecraft:crafting_table")).bindCraftModel(craft);
		return craft;
	}
}
