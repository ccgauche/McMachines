package com.ccgauche.mcmachines.data;

import java.util.Optional;
import java.util.UUID;

import com.ccgauche.mcmachines.ExampleMod;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

/*
A util class copied from minecraft source code.
 */
public class Attribute {

	public AttributeType type;
	public Optional<OperationType> operation;
	public Float amount;
	public Optional<SlotType> slot;

	public void addTo(NbtList list) {
		ExampleMod.KKK++;
		NbtCompound compound = new NbtCompound();
		// UUID:[I;-12259,31567,22150,-63134]}
		compound.putString("AttributeName", type.toString());
		compound.putFloat("Amount", amount);
		compound.putUuid("UUID", new UUID(239594306, 8436036 + ExampleMod.KKK));
		compound.putInt("Operation", operation.orElseGet(() -> OperationType.Add).getOperation());
		slot.ifPresent(slotType -> compound.putString("Slot", slotType.name().toLowerCase()));
		compound.putString("Name", type.toString());
		list.add(compound);
	}
}
