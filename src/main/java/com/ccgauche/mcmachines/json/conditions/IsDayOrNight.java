package com.ccgauche.mcmachines.json.conditions;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.machine.IMachine;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.NotNull;

public record IsDayOrNight(boolean day) implements ICondition{


    @Override
    public boolean isTrue(@NotNull IMachine machine, @NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull DataCompound data) {
        return day == (world.getLightLevel(LightType.SKY, pos) > 14 && day());
    }

    private boolean day(@NotNull ServerWorld world) {
        long time = world.getTimeOfDay() % 24000;
        return time < 12300 || time > 23850;
    }
}
