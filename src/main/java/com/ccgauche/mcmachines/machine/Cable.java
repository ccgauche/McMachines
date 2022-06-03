package com.ccgauche.mcmachines.machine;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.utils.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Cable implements IMachine {

	private static final Cable instance = new Cable();

	private static final HashMap<Block, Integer[]> materials = new HashMap<>();

	static {
		materials.put(Blocks.IRON_BLOCK, new Integer[] { 100, 400, 50 });
		materials.put(Blocks.GOLD_BLOCK, new Integer[] { 200, 1000, 10 });
		materials.put(Blocks.REDSTONE_BLOCK, new Integer[] { 10, 20_000, 0 });
		materials.put(Blocks.NETHERITE_BLOCK, new Integer[] { 10_000, 1_000_000, 0 });
		materials.put(Blocks.AMETHYST_BLOCK, new Integer[] { 100, 100, 100 });
		materials.put(Blocks.COPPER_BLOCK, new Integer[] { 200, 200, 5 });
		materials.put(Blocks.WAXED_COPPER_BLOCK, new Integer[] { 200, 200, 5 });
	}

	@Override
	public String id() {
		return "base:cable";
	}

	@Override
	public void place(World world, BlockPos pos, DataCompound properties) {
		IMachine.super.place(world, pos, properties);
		System.out.println(world.getBlockState(pos).getBlock());
		Integer[] b = materials.get(world.getBlockState(pos).getBlock());
		if (b == null)
			new Exception("Unexpected place at invalid block material").printStackTrace();
		DataRegistry.ENERGY_MAX.set(world, pos, b[1]);
		DataRegistry.ENERGY_IN.set(world, pos, b[0]);
		DataRegistry.ENERGY_OUT.set(world, pos, b[0]);
		DataRegistry.HOLD_RESISTANCE.set(world, pos, b[2]);
	}

	@Override
	public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
		Integer[] b = materials.get(world.getBlockState(pos).getBlock());
		if (b == null) {
			System.out.println("Can't find cable for " + world.getBlockState(pos).getBlock());
			DataRegistry.getMap(world).remove(pos);
			return;
		}
		if (world.isReceivingRedstonePower(pos))
			return;
		applyCableTransform(pos, world);
	}

	public static void applyCableTransform(BlockPos pos, World world) {
		int energyOutMax = DataRegistry.ENERGY_OUT.getOrDefault(world, pos, 0);
		int energyInMax = DataRegistry.ENERGY_IN.getOrDefault(world, pos, 0);
		int energy = DataRegistry.ENERGY_CONTENT.getOrDefault(world, pos, 0);
		int holdResistance = DataRegistry.HOLD_RESISTANCE.getOrDefault(world, pos, 0);
		energy = (int) (((float) energy) * ((float) ((1000 - holdResistance)) / 1000f));
		int energyMax = DataRegistry.ENERGY_MAX.getOrDefault(world, pos, 0);
		if (energyOutMax == 0)
			return;
		for (Direction dir : new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
				Direction.EAST, Direction.WEST }) {
			if (energy == 0 || energyOutMax == 0)
				break;
			BlockPos blockFacing = ItemUtils.getBlockFacing(dir, pos);
			int otherEnergyInMax = DataRegistry.ENERGY_IN.getOrDefault(world, blockFacing, 0);
			int otherEnergyOutMax = DataRegistry.ENERGY_OUT.getOrDefault(world, blockFacing, 0);
			Integer[] b = materials.get(world.getBlockState(blockFacing).getBlock());
			if (b != null && otherEnergyOutMax == 0) {
				instance.place(world, blockFacing, new DataCompound());
				otherEnergyInMax = DataRegistry.ENERGY_IN.getOrDefault(world, blockFacing, 0);
				otherEnergyOutMax = DataRegistry.ENERGY_OUT.getOrDefault(world, blockFacing, 0);
			}
			if (otherEnergyInMax == 0)
				continue;
			int otherEnergy = DataRegistry.ENERGY_CONTENT.getOrDefault(world, blockFacing, 0);
			int otherEnergyMax = DataRegistry.ENERGY_MAX.getOrDefault(world, blockFacing, 0);

			int otherAccept = otherEnergyMax - otherEnergy;

			int maxTransfer = Math.min(energy, Math.min(otherAccept, Math.min(otherEnergyInMax, energyOutMax)));

			if (energyInMax != 0 && otherEnergyOutMax != 0) {
				int ratio = (int) Math.ceil(((float) (energy * otherEnergyMax)) / ((float) energyMax));
				maxTransfer = Math.max(0, Math.min(maxTransfer, Math.max(ratio - otherEnergy, energy - otherEnergy)));
			}

			if (maxTransfer == 0)
				continue;
			energy -= maxTransfer;
			energyOutMax -= maxTransfer;
			otherEnergy = otherEnergy + maxTransfer;
			DataRegistry.ENERGY_CONTENT.set(world, blockFacing, otherEnergy);
		}
		DataRegistry.ENERGY_CONTENT.set(world, pos, energy);
	}
}