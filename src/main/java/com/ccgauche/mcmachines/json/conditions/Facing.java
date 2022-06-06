package com.ccgauche.mcmachines.json.conditions;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.machine.IMachine;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

public class Facing implements ICondition {

	@NotNull
	private final ICondition condition;

	public Facing(@NotNull ICondition condition) {
		this.condition = condition;
	}

	@Override
	public boolean isTrue(@NotNull IMachine machine, @NotNull ServerWorld world, @NotNull BlockPos pos,
			@NotNull DataCompound data) {
		return condition.isTrue(machine, world, pos.offset(world.getBlockState(pos).get(Properties.FACING)), data);
	}
}
