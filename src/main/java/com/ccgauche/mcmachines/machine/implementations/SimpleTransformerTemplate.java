package com.ccgauche.mcmachines.machine.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.conditions.ICondition;
import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.json.recipe.TransformerCraft;
import com.ccgauche.mcmachines.machine.ICraftingMachine;
import com.ccgauche.mcmachines.machine.IMachine;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.utils.ItemUtils;

import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimpleTransformerTemplate implements IMachine, ICraftingMachine {

	private final List<TransformerCraft> crafts = new ArrayList<>();

	private final @NotNull String id;
	private final @NotNull String name;
	private final @Nullable DataCompound properties;
	private final @Nullable ICondition conditions;

	private final IItem item;

	public SimpleTransformerTemplate(@NotNull String id, @NotNull String name, @Nullable DataCompound properties,
			@Nullable ICondition conditions) {
		this.id = id;
		this.name = name;
		this.properties = properties;
		this.conditions = conditions;
		item = new IItem.Basic(Items.DROPPER, this.name, this.id, this.properties, List.of());
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public void place(World world, BlockPos pos, DataCompound properties) {
		IMachine.super.place(world, pos, properties);
		DataRegistry.write(world, pos, this.properties);
		DataRegistry.write(world, pos, properties);
	}

	@Override
	public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
		DropperBlockEntity furnaceBlock = (DropperBlockEntity) world.getBlockEntity(pos);
		if (furnaceBlock == null)
			return;
		if (conditions != null && !conditions.isTrue(this, (ServerWorld) world, pos, object)) {
			return;
		}
		int k = DataRegistry.ENERGY_CONTENT.getOrDefault(object, 0);
		for (TransformerCraft craft : crafts) {
			if (k < craft.energy_use
					|| !craft.inputs.stream().allMatch(e -> containsEnoughItem(e.first(), e.second(), furnaceBlock))) {
				continue;
			}
			craft.inputs.forEach(e -> removeItem(e.first(), e.second(), furnaceBlock));
			DataRegistry.ENERGY_CONTENT.set(object, k - craft.energy_use);
			for (var toDrop : craft.outputs) {
				ItemStack stack = toDrop.first().copy();
				int multiplier = toDrop.second() / 100 + ((new Random().nextInt(100) <= toDrop.second() % 100) ? 1 : 0);
				stack.setCount(stack.getCount() * multiplier);
				if (stack.getCount() == 0) {
					continue;
				}
				ItemUtils.dispense((ServerWorld) world, pos, stack);
			}
			break;

		}
	}

	public static boolean containsEnoughItem(CItem cItem, int number, DropperBlockEntity inv) {
		for (int i = 0; i < 9; i++) {
			ItemStack item = inv.getStack(i);
			if (item == null || item.getItem() == null || item.isEmpty())
				continue;
			CItem cItem1 = new CItem(item);
			if (cItem1.equals(cItem)) {
				number -= item.getCount();
				if (number < 1) {
					return true;
				}
			}
		}
		return false;
	}

	public static void removeItem(CItem cItem, int number, DropperBlockEntity inv) {
		for (int i = 0; i < 9; i++) {
			ItemStack item = inv.getStack(i);
			if (item == null || item.getItem() == null || item.isEmpty())
				continue;
			CItem cItem1 = new CItem(item);
			if (cItem1.equals(cItem)) {
				int toRemove = Math.min(item.getCount(), number);
				number -= toRemove;
				ItemUtils.removeItem(inv, i, toRemove);
				if (number == 0) {
					return;
				}
			}
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
