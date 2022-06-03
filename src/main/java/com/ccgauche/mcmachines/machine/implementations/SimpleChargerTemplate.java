package com.ccgauche.mcmachines.machine.implementations;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.conditions.ICondition;
import com.ccgauche.mcmachines.machine.IMachine;
import com.ccgauche.mcmachines.registry.DataRegistry;

import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimpleChargerTemplate implements IMachine {

	private final @NotNull String id;
	private final @NotNull String name;
	private final @Nullable DataCompound properties;
	private final @Nullable ICondition conditions;

	@NotNull
	private final IItem item;

	public SimpleChargerTemplate(@NotNull String id, @NotNull String name, @Nullable DataCompound properties,
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
		if (world.isReceivingRedstonePower(pos))
			return;
		if (conditions != null && !conditions.isTrue(this, (ServerWorld) world, pos, object)) {
			return;
		}
		int e = DataRegistry.ENERGY_CONTENT.getOrDefault(object, 0);
		int k = DataRegistry.CHARGE_PER_TICK.getOrDefault(object, 0);
		if (k == 0 || e == 0)
			return;
		for (int i = 0; i < 9; i++) {
			ItemStack item = furnaceBlock.getStack(i);
			if (item == null || item.getItem() == null || item.isEmpty())
				continue;

			DataCompound compound = new DataCompound(item);
			int maxE = DataRegistry.ENERGY_MAX.getOrDefault(compound, 0);
			if (maxE == 0 || k <= 0) {
				continue;
			}
			int ec = DataRegistry.ENERGY_CONTENT.getOrDefault(compound, 0);
			if (ec >= maxE) {
				continue;
			}
			int maxToAdd = Math.min(e, Math.min((maxE - ec), k));
			k -= maxToAdd;
			e -= maxToAdd;
			DataRegistry.ENERGY_CONTENT.set(compound, ec + maxToAdd);
			compound.updateStack(item);
			if (k == 0 || e == 0)
				break;
		}
		DataRegistry.ENERGY_CONTENT.set(object, e);

	}

	public IItem getRegistryItem() {
		return item;
	}
}
