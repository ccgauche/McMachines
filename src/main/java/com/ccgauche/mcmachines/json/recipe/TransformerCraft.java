package com.ccgauche.mcmachines.json.recipe;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.Dual;
import com.ccgauche.mcmachines.json.conditions.ICondition;

import net.minecraft.item.ItemStack;

@SuppressWarnings("NotNullFieldNotInitialized")
public class TransformerCraft implements IRecipe {

	@NotNull
	public String on;
	public Integer energy_use;
	@NotNull
	public List<@NotNull Dual<@NotNull CItem, @NotNull Integer>> inputs;
	@NotNull
	public List<@NotNull Dual<@NotNull ItemStack, @NotNull Integer>> outputs;
	public Optional<ICondition> conditions;

	@Override
	public String on() {
		return on;
	}
}
