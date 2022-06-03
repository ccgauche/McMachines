package com.ccgauche.mcmachines.json.recipe;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.Dual;

import net.minecraft.item.ItemStack;

@SuppressWarnings("NotNullFieldNotInitialized")
public class GeneratorCraft implements IRecipe {

	@NotNull
	public String on;
	@NotNull
	public List<@NotNull Dual<@NotNull CItem, @NotNull Integer>> inputs;
	@NotNull
	public List<@NotNull Dual<@NotNull ItemStack, @NotNull Integer>> outputs;
	public Integer energy_production;
	public Integer tick_duration;

	@Override
	public String on() {
		return on;
	}
}
