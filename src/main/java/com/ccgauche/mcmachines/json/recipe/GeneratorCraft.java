package com.ccgauche.mcmachines.json.recipe;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.Dual;
import com.ccgauche.mcmachines.json.conditions.ICondition;

import net.minecraft.item.ItemStack;

@SuppressWarnings("NotNullFieldNotInitialized")
public class GeneratorCraft implements IRecipe {

	@NotNull
	public String on;
	@NotNull
	public List<@NotNull Dual<@NotNull CItem, @NotNull Integer>> inputs;
	@NotNull
	public List<@NotNull Dual<@NotNull ItemStack, @NotNull Integer>> outputs;
	@NotNull
	public Integer energy_production;
	@NotNull
	public Integer tick_duration;
	public Optional<ICondition> conditions;
	public Optional<ICondition> post_craft;

	@Override
	@NotNull
	public String on() {
		return on;
	}
}
