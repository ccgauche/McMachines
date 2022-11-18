package com.ccgauche.mcmachines.machine.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.recipe.GeneratorCraft;
import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.machine.Cable;
import com.ccgauche.mcmachines.machine.ICraftingMachine;
import com.ccgauche.mcmachines.machine.IMachine;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.utils.ItemUtils;

import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A machine that generate energy from fuel
 */
public class BurningGeneratorTemplate extends IMachine implements ICraftingMachine {

	private final List<GeneratorCraft> crafts = new ArrayList<>();
	private final @NotNull String id;
	private final @NotNull String name;
	private final @Nullable DataCompound properties;
	// private final @Nullable ICondition conditions;

	@NotNull
	private final IItem item;

	public BurningGeneratorTemplate(@NotNull CItem base, @NotNull String id, @NotNull String name,
			@Nullable DataCompound properties/* , @Nullable ICondition conditions */) {
		this.id = id;
		this.name = name;
		this.properties = properties;
		// this.conditions = conditions;
		item = new IItem.Basic(base.getItemOrCrash(), this.name, this.id, this.properties, List.of());
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
		if (world.isReceivingRedstonePower(pos))
			return;
		Inventory furnaceBlock = (Inventory) world.getBlockEntity(pos);
		Cable.applyCableTransform(pos, world);
		int burningTicks = DataRegistry.GEN_REMAINING_SECS.getOrDefault(object, 0);
		int genPerTick = DataRegistry.GEN_PER_TICK.getOrDefault(object, 0);
		if (burningTicks > 0) {
			int max = DataRegistry.ENERGY_MAX.getOrDefault(object, 0);
			int energy = DataRegistry.ENERGY_CONTENT.getOrDefault(object, 0);
			if (max < energy + genPerTick)
				return;
			DataRegistry.ENERGY_CONTENT.set(object, Math.min(max, energy + genPerTick));
			DataRegistry.GEN_REMAINING_SECS.set(object, burningTicks - 1);
			return;
		}

		burningTicks = DataRegistry.GEN_REMAINING_SECS.getOrDefault(object, 0);
		if (burningTicks > 0)
			return;
		for (var craft : crafts) {
			if (!craft.inputs.stream()
					.allMatch(e -> ItemUtils.containsEnoughItem(e.first(), e.second(), furnaceBlock))) {
				continue;
			}
			if (!craft.preCraft.stream().allMatch(e -> e.tick(world, pos, object))) {
				continue;
			}
			DataRegistry.GEN_REMAINING_SECS.set(object, craft.tick_duration);
			DataRegistry.GEN_PER_TICK.set(object, craft.energy_production);
			ItemUtils.outputItems((ServerWorld) world, pos, craft.outputs);
			craft.postCraft.forEach(e -> e.tick(world, pos, object));
			break;
		}
	}

	public @NotNull String name() {
		return name;
	}

	public @Nullable DataCompound properties() {
		return properties;
	}

	/*
	 * public @Nullable ICondition conditions() { return conditions; }
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (BurningGeneratorTemplate) obj;
		return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name)
				&& Objects.equals(this.properties,
						that.properties)/* && Objects.equals(this.conditions, that.conditions) */;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, properties/* , conditions */);
	}

	@Override
	public String toString() {
		return "ConstantGeneratorTemplate[" + "id=" + id + ", " + "name=" + name + ", " + "properties=" + properties
		/* + ", " + "conditions=" + conditions */ + ']';
	}

	@Override
	public Class<? extends IRecipe> getCraftModelType() {
		return GeneratorCraft.class;
	}

	@Override
	public void bindCraftModel(@NotNull IRecipe recipe) {
		if (recipe instanceof GeneratorCraft craft) {
			crafts.add(craft);
		} else {
			System.out.println("Invalid recipe type " + recipe.getClass().getSimpleName());
		}
	}

	@Override
	public String getCraftModelRegistryKey() {
		return id();
	}
}
