package com.ccgauche.mcmachines.machine.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.machine.Cable;
import com.ccgauche.mcmachines.machine.IMachine;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.registry.events.MachineTickListener;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A machine that generate energy each tick
 */
public final class ConstantGeneratorTemplate extends IMachine {
	private final @NotNull String id;
	private final @NotNull String name;
	private final @Nullable DataCompound properties;
	private final @NotNull List<MachineTickListener> conditions = new ArrayList<>();

	@NotNull
	private final IItem item;

	public ConstantGeneratorTemplate(@NotNull CItem base, @NotNull String id, @NotNull String name,
			@Nullable DataCompound properties) {
		this.id = id;
		this.name = name;
		this.properties = properties;
		item = new IItem.Basic(base.getItemOrCrash(), this.name, this.id, this.properties, List.of());
	}

	public void addCondition(MachineTickListener listener) {
		conditions.add(listener);
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
		super.place(world, pos, properties);
		DataRegistry.write(world, pos, this.properties);
		DataRegistry.write(world, pos, properties);
	}

	@Override
	public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
		Cable.applyCableTransform(pos, world);
		if (!conditions.stream().allMatch(e -> e.tick(world, pos, object))) {
			return;
		}
		int max = DataRegistry.ENERGY_MAX.getOrDefault(object, 0);
		int energy = DataRegistry.ENERGY_CONTENT.getOrDefault(object, 0);
		DataRegistry.ENERGY_CONTENT.set(object,
				Math.min(max, energy + DataRegistry.GEN_PER_TICK.getOrDefault(object, 0)));
	}

	public @NotNull String name() {
		return name;
	}

	public @Nullable DataCompound properties() {
		return properties;
	}

	public @NotNull List<MachineTickListener> conditions() {
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
