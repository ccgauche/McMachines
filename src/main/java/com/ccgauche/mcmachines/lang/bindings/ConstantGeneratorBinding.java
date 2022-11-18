package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.lang.CodeParser;
import com.ccgauche.mcmachines.lang.Context;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;
import com.ccgauche.mcmachines.machine.implementations.ConstantGeneratorTemplate;
import com.ccgauche.mcmachines.registry.ItemRegistry;
import com.ccgauche.mcmachines.registry.MachineRegistry;

import net.minecraft.item.Items;

@TypeAlias(name = "ConstantGenerator")
public class ConstantGeneratorBinding {

	public static ConstantGeneratorTemplate $new(String id, String name, DataCompound compound) {
		return new ConstantGeneratorTemplate(new CItem(Items.FURNACE), id, name, compound);
	}

	@Method
	public static ConstantGeneratorTemplate register(ConstantGeneratorTemplate machine) {
		MachineRegistry.add(machine);
		ItemRegistry.addItem(machine.getRegistryItem());
		return machine;
	}

	@Method
	public static ConstantGeneratorTemplate onPreTick(Context ctx, CodeParser.ClosureFunction func,
			ConstantGeneratorTemplate machine) {
		machine.preTickListeners
				.add((world, pos, compound) -> (boolean) func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static ConstantGeneratorTemplate onPostTick(Context ctx, CodeParser.ClosureFunction func,
			ConstantGeneratorTemplate machine) {
		machine.postTickListeners.add((world, pos, compound) -> func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static ConstantGeneratorTemplate onTick(Context ctx, CodeParser.ClosureFunction function,
			ConstantGeneratorTemplate machine) {
		machine.conditions()
				.add((world, pos, compound) -> (boolean) function.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}
}
