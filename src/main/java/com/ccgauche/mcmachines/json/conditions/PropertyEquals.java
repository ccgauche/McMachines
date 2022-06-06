package com.ccgauche.mcmachines.json.conditions;

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.machine.IMachine;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;

public class PropertyEquals implements ICondition {

	private Property<?> property;
	private List<Object> object;

	public PropertyEquals(List<String> s) {
		if (s.get(0).equalsIgnoreCase("AGE_7")) {
			property = Properties.AGE_7;
			object = s.stream().skip(1).map(Integer::parseInt).collect(Collectors.toList());
		} else {
			System.out.println("Unknown property " + s.get(0));
			System.exit(0);
		}
	}

	@Override
	public boolean isTrue(@NotNull IMachine machine, @NotNull ServerWorld world, @NotNull BlockPos pos,
			@NotNull DataCompound data) {
		Object comparable = world.getBlockState(pos).getEntries().get(property);
		if (comparable == null) {
			return object.size() == 0;
		}
		return object.stream().anyMatch(e -> e.equals(comparable));
	}
}
