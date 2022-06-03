package com.ccgauche.mcmachines.machine.implementations;

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.conditions.ICondition;
import com.ccgauche.mcmachines.machine.Cable;
import com.ccgauche.mcmachines.machine.IMachine;
import com.ccgauche.mcmachines.registry.DataRegistry;

import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ConstantGeneratorTemplate implements IMachine {
	private final @NotNull String id;
	private final @NotNull String name;
	private final @Nullable DataCompound properties;
	private final @Nullable ICondition conditions;

	@NotNull
	private final IItem item;

	public ConstantGeneratorTemplate(@NotNull String id, @NotNull String name, @Nullable DataCompound properties,
			@Nullable ICondition conditions) {
		this.id = id;
		this.name = name;
		this.properties = properties;
		this.conditions = conditions;
		item = new IItem.Basic(Items.DROPPER, this.name, this.id, this.properties, List.of());
	}

	public String id() {
		return id;
	}

	@NotNull
	public IItem getRegistryItem() {
		return item;
	}

	@Override
	public void place(World world, BlockPos pos, DataCompound properties) {
		IMachine.super.place(world, pos, properties);
		DataRegistry.write(world, pos, this.properties);
		DataRegistry.write(world, pos, properties);
	}

	@Override
	public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
		if (world.isReceivingRedstonePower(pos))
			return;
		Cable.applyCableTransform(pos, world);
		if (conditions != null && !conditions.isTrue(this, (ServerWorld) world, pos, object)) {
			return;
		}
		int max = DataRegistry.ENERGY_MAX.getOrDefault(world, pos, 0);
		int energy = DataRegistry.ENERGY_CONTENT.getOrDefault(world, pos, 0);
		DataRegistry.ENERGY_CONTENT.set(world, pos,
				Math.min(max, energy + DataRegistry.GEN_PER_TICK.getOrDefault(world, pos, 0)));
	}

	public @NotNull String name() {
		return name;
	}

	public @Nullable DataCompound properties() {
		return properties;
	}

	public @Nullable ICondition conditions() {
		return conditions;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (ConstantGeneratorTemplate) obj;
		return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name)
				&& Objects.equals(this.properties, that.properties) && Objects.equals(this.conditions, that.conditions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, properties, conditions);
	}

	@Override
	public String toString() {
		return "ConstantGeneratorTemplate[" + "id=" + id + ", " + "name=" + name + ", " + "properties=" + properties
				+ ", " + "conditions=" + conditions + ']';
	}

}
