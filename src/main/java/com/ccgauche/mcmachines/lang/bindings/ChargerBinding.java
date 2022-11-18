package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.lang.CodeParser;
import com.ccgauche.mcmachines.lang.Context;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;
import com.ccgauche.mcmachines.machine.implementations.SimpleChargerTemplate;
import com.ccgauche.mcmachines.registry.ItemRegistry;
import com.ccgauche.mcmachines.registry.MachineRegistry;

import net.minecraft.item.Items;

@TypeAlias(name = "Charger")
public class ChargerBinding {

	public static SimpleChargerTemplate $new(String id, String name, DataCompound properties) {
		return new SimpleChargerTemplate(new CItem(Items.DROPPER), id, name, properties);
	}

	@Method
	public static SimpleChargerTemplate onPreTick(Context ctx, CodeParser.ClosureFunction func,
			SimpleChargerTemplate machine) {
		machine.preTickListeners
				.add((world, pos, compound) -> (boolean) func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static SimpleChargerTemplate onPostTick(Context ctx, CodeParser.ClosureFunction func,
			SimpleChargerTemplate machine) {
		machine.postTickListeners.add((world, pos, compound) -> func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static SimpleChargerTemplate register(SimpleChargerTemplate machine) {
		MachineRegistry.add(machine);
		ItemRegistry.addItem(machine.getRegistryItem());
		return machine;
	}
}
