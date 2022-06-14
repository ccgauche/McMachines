package com.ccgauche.mcmachines.json;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.json.conditions.ICondition;

@SuppressWarnings("ALL")
public class Machine {

	@NotNull
	public String name;
	@NotNull
	public Optional<CItem> base;

	@NotNull
	public MachineMode mode;
	public Optional<DataCompound> properties;
	public Optional<ICondition> conditions;

	@Override
	public String toString() {
		return "Machine{" + "name='" + name + '\'' + ", mode='" + mode + '\'' + ", properties=" + properties
				+ ", conditions=" + conditions + '}';
	}
}
