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
public class TransformerCraft implements IRecipe {

	public final int energy_use;
	@NotNull
	public final List<@NotNull Dual<@NotNull CItem, @NotNull Integer>> inputs;
	@NotNull
	public final List<@NotNull Dual<@NotNull ItemStack, @NotNull Integer>> outputs;

	@NotNull
	public final List<@NotNull MachineTickListener> preCraft;

	@NotNull
	public final List<@NotNull MachinePostTickListener> postCraft;

	public TransformerCraft(int energy_use, @NotNull List<@NotNull Dual<@NotNull CItem, @NotNull Integer>> inputs,
			@NotNull List<@NotNull Dual<@NotNull ItemStack, @NotNull Integer>> outputs) {
		this.energy_use = energy_use;
		this.inputs = inputs;
		this.outputs = outputs;
		this.preCraft = new ArrayList<>();
		this.postCraft = new ArrayList<>();
	}
}
