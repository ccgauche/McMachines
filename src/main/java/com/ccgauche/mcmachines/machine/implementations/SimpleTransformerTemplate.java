package com.ccgauche.mcmachines.machine.implementations;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.json.recipe.TransformerCraft;
import com.ccgauche.mcmachines.machine.ICraftingMachine;
import com.ccgauche.mcmachines.machine.IMachine;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.utils.ItemUtils;

import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A machine that transform items into other items using energy
 */
public class SimpleTransformerTemplate extends IMachine implements ICraftingMachine {

	private final List<TransformerCraft> crafts = new ArrayList<>();

	private final @NotNull String id;
	private final @NotNull String name;
	private final @Nullable DataCompound properties;

	@NotNull
	private final IItem item;

	public SimpleTransformerTemplate(@NotNull CItem base, @NotNull String id, @NotNull String name,
			@Nullable DataCompound properties) {
		this.id = id;
		this.name = name;
		this.properties = properties;
		item = new IItem.Basic(base.getItemOrCrash(), this.name, this.id, this.properties, List.of());
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public void place(World world, BlockPos pos, DataCompound properties) {
		super.place(world, pos, properties);
		DataRegistry.write(world, pos, this.properties);
		DataRegistry.write(world, pos, properties);
	}

	@Override
	public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
		Inventory furnaceBlock = (Inventory) world.getBlockEntity(pos);
		if (furnaceBlock == null)
			return;
		if (world.isReceivingRedstonePower(pos))
			return;
		int k = DataRegistry.ENERGY_CONTENT.getOrDefault(object, 0);
		for (TransformerCraft craft : crafts) {
			if (k < craft.energy_use || !craft.inputs.stream()
					.allMatch(e -> ItemUtils.containsEnoughItem(e.first(), e.second(), furnaceBlock))) {
				continue;
			}
			if (!craft.preCraft.stream().allMatch(e -> e.tick(world, pos, object))) {
				continue;
			}
			craft.inputs.forEach(e -> ItemUtils.removeItem(e.first(), e.second(), furnaceBlock));
			DataRegistry.ENERGY_CONTENT.set(object, k - craft.energy_use);
			ItemUtils.outputItems((ServerWorld) world, pos, craft.outputs);
			craft.postCraft.forEach(e -> e.tick(world, pos, object));
			return;
		}
	}

	@Override
	public Class<? extends IRecipe> getCraftModelType() {
		return TransformerCraft.class;
	}

	@Override
	public void bindCraftModel(@NotNull IRecipe recipe) {
		if (recipe instanceof TransformerCraft craft) {
			crafts.add(craft);
		} else {
			System.out.println("Invalid recipe type " + recipe.getClass().getSimpleName());
		}
	}

	@Override
	public String getCraftModelRegistryKey() {
		return id();
	}

	public IItem getRegistryItem() {
		return item;
	}
}
