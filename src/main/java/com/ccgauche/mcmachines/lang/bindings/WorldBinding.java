package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;

import net.minecraft.world.World;

@TypeAlias(name = "World")
public class WorldBinding {

	@Method
	public static boolean isDay(World world) {
		long time = world.getTimeOfDay() % 24000;
		return time < 12300 || time > 23850;
	}

	public static boolean isNight(World world) {
		return !isDay(world);
	}
}
