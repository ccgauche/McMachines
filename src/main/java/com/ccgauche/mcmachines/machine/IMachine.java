package com.ccgauche.mcmachines.machine;

import com.ccgauche.mcmachines.data.GlobalKeys;
import com.ccgauche.mcmachines.data.DataCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface IMachine {

    String id();

    default void place(World world, BlockPos pos, DataCompound properties) {
        GlobalKeys.ID.set(pos, this.id());
    }

    void tick(@NotNull DataCompound object, World world, BlockPos pos);
}
