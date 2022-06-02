package com.ccgauche.mcmachines.machine;

import com.ccgauche.mcmachines.data.GlobalKeys;
import com.ccgauche.mcmachines.data.DataCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class Generator implements IMachine {
    @Override
    public String id() {
        return "base:generator";
    }

    @Override
    public void place(World world, BlockPos pos, DataCompound properties) {
        IMachine.super.place(world,pos,properties);
        GlobalKeys.ENERGY_MAX.set(pos, 10_000);
        GlobalKeys.ENERGY_OUT.set(pos, 1_000);
    }

    @Override
    public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
        Cable.applyCableTransform(pos,world);
        int max = GlobalKeys.ENERGY_MAX.getOrDefault(pos, 0);
        int energy = GlobalKeys.ENERGY_CONTENT.getOrDefault(pos, 0);
        GlobalKeys.ENERGY_CONTENT.set(pos, Math.min(max, energy + 10));
    }
}
