package com.ccgauche.mcmachines.machine;

import com.ccgauche.mcmachines.utils.ItemUtils;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.GlobalKeys;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Cable implements IMachine {

    private static final Cable instance = new Cable();

    private static final HashMap<Block, Integer[]> materials = new HashMap<>();

    static {
        materials.put(Blocks.IRON_BLOCK, new Integer[]{100, 400, 50});
        materials.put(Blocks.GOLD_BLOCK, new Integer[]{200, 1000, 10});
        materials.put(Blocks.REDSTONE_BLOCK, new Integer[]{10, 20_000, 0});
        materials.put(Blocks.NETHERITE_BLOCK, new Integer[]{10_000, 1_000_000, 0});
        materials.put(Blocks.AMETHYST_BLOCK, new Integer[]{100, 100, 100});
        materials.put(Blocks.COPPER_BLOCK, new Integer[]{200, 200, 5});
        materials.put(Blocks.WAXED_COPPER_BLOCK, new Integer[]{200, 200, 5});
    }

    @Override
    public String id() {
        return "base:cable";
    }

    @Override
    public void place(World world, BlockPos pos, DataCompound properties) {
        IMachine.super.place(world,pos,properties);
        System.out.println(world.getBlockState(pos).getBlock());
        Integer[] b = materials.get(world.getBlockState(pos).getBlock());
        if (b == null)
            new Exception("Unexpected place at invalid block material").printStackTrace();
        GlobalKeys.ENERGY_MAX.set(pos, b[1]);
        GlobalKeys.ENERGY_IN.set(pos, b[0]);
        GlobalKeys.ENERGY_OUT.set(pos, b[0]);
        GlobalKeys.HOLD_RESISTANCE.set(pos, b[2]);
    }

    @Override
    public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
        Integer[] b = materials.get(world.getBlockState(pos).getBlock());
        if (b == null) {
            GlobalKeys.entries().remove(pos);
            return;
        }
        applyCableTransform(pos, world);
    }

    public static void applyCableTransform(BlockPos pos, World world) {
        int energyOutMax = GlobalKeys.ENERGY_OUT.getOrDefault(pos, 0);
        int energyInMax = GlobalKeys.ENERGY_IN.getOrDefault(pos, 0);
        int energy = GlobalKeys.ENERGY_CONTENT.getOrDefault(pos, 0);
        int holdResistance = GlobalKeys.HOLD_RESISTANCE.getOrDefault(pos, 0);
        energy = (int)(((float)energy) * ((float)((1000 - holdResistance)) / 1000f));
        int energyMax = GlobalKeys.ENERGY_MAX.getOrDefault(pos, 0);
        if (energyOutMax == 0) return;
        for (Direction dir : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            if (energy == 0 || energyOutMax == 0)
                break;
            BlockPos blockFacing = ItemUtils.getBlockFacing(dir, pos);
            int otherEnergyInMax = GlobalKeys.ENERGY_IN.getOrDefault(blockFacing, 0);
            int otherEnergyOutMax = GlobalKeys.ENERGY_OUT.getOrDefault(blockFacing, 0);
            Integer[] b = materials.get(world.getBlockState(blockFacing).getBlock());
            if (b != null && otherEnergyOutMax == 0) {
                instance.place(world, blockFacing, new DataCompound());
                otherEnergyInMax = GlobalKeys.ENERGY_IN.getOrDefault(blockFacing, 0);
                otherEnergyOutMax = GlobalKeys.ENERGY_OUT.getOrDefault(blockFacing, 0);
            }
            if (otherEnergyInMax == 0)
                continue;
            int otherEnergy = GlobalKeys.ENERGY_CONTENT.getOrDefault(blockFacing, 0);
            int otherEnergyMax = GlobalKeys.ENERGY_MAX.getOrDefault(blockFacing, 0);

            int otherAccept = otherEnergyMax - otherEnergy;

            int maxTransfer = Math.min(energy, Math.min(otherAccept, Math.min(otherEnergyInMax, energyOutMax)));

            if (energyInMax != 0 && otherEnergyOutMax != 0) {
                int ratio = (int) Math.ceil(((float) (energy * otherEnergyMax)) / ((float)energyMax));
                maxTransfer = Math.max(0, Math.min(maxTransfer, Math.max(ratio - otherEnergy, energy - otherEnergy)));
            }

            if (maxTransfer == 0) continue;
            energy -= maxTransfer;
            energyOutMax -= maxTransfer;
            otherEnergy = otherEnergy+maxTransfer;
            GlobalKeys.ENERGY_CONTENT.set(blockFacing, otherEnergy);
        }
        GlobalKeys.ENERGY_CONTENT.set(pos, energy);
    }
}