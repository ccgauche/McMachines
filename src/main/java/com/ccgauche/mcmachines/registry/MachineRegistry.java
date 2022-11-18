package com.ccgauche.mcmachines.registry;

import java.util.HashMap;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.machine.Cable;
import com.ccgauche.mcmachines.machine.ICraftingMachine;
import com.ccgauche.mcmachines.machine.IMachine;

/**
 * The registry in which all machines are stored
 */
public final class MachineRegistry {

	@NotNull
	private final static HashMap<String, IMachine> map = new HashMap<>();

	static {
		add(new Cable());
	}

	@NotNull
	public static HashMap<String, IMachine> get() {
		return map;
	}

	@Nullable
	@Contract("null -> null")
	public static IMachine get(@Nullable String machine) {
		return map.get(machine);
	}

	public static void add(@NotNull IMachine machine) {
		map.put(machine.id(), machine);
		if (machine instanceof ICraftingMachine m) {
			CraftRegistry.add(m);
		}
	}
}
