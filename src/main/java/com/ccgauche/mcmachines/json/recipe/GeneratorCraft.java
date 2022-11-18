package com.ccgauche.mcmachines.json.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.Dual;
import com.ccgauche.mcmachines.registry.events.MachinePostTickListener;
import com.ccgauche.mcmachines.registry.events.MachineTickListener;

import net.minecraft.item.ItemStack;

@SuppressWarnings("NotNullFieldNotInitialized")
public class GeneratorCraft implements IRecipe {

	@NotNull
	public final List<@NotNull Dual<@NotNull CItem, @NotNull Integer>> inputs;
	@NotNull
	public final List<@NotNull Dual<@NotNull ItemStack, @NotNull Integer>> outputs;
	@NotNull
	public final int energy_production;
	@NotNull
	public final int tick_duration;

	@NotNull
	public final List<@NotNull MachineTickListener> preCraft = new ArrayList<>();

	@NotNull
	public final List<@NotNull MachinePostTickListener> postCraft = new ArrayList<>();

	public GeneratorCraft(@NotNull List<@NotNull Dual<@NotNull CItem, @NotNull Integer>> inputs,
			@NotNull List<@NotNull Dual<@NotNull ItemStack, @NotNull Integer>> outputs, @NotNull int energy_production,
			@NotNull int tick_duration) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.energy_production = energy_production;
		this.tick_duration = tick_duration;
	}
}
