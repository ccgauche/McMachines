package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.json.recipe.GeneratorCraft;
import com.ccgauche.mcmachines.lang.CodeParser;
import com.ccgauche.mcmachines.lang.Context;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;
import com.ccgauche.mcmachines.machine.implementations.BurningGeneratorTemplate;
import com.ccgauche.mcmachines.registry.ItemRegistry;
import com.ccgauche.mcmachines.registry.MachineRegistry;

import net.minecraft.item.Items;

@TypeAlias(name = "BurningGenerator")
public class BurningGeneratorBinding {

	public static BurningGeneratorTemplate $new(String id, String name, DataCompound properties) {
		return new BurningGeneratorTemplate(new CItem(Items.DROPPER), id, name, properties);
	}

	@Method
	public static BurningGeneratorTemplate addRecipe(GeneratorCraft craft, BurningGeneratorTemplate machine) {
		machine.bindCraftModel(craft);
		return machine;
	}

	@Method
	public static BurningGeneratorTemplate onPreTick(Context ctx, CodeParser.ClosureFunction func,
			BurningGeneratorTemplate machine) {
		machine.preTickListeners
				.add((world, pos, compound) -> (boolean) func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static BurningGeneratorTemplate onPostTick(Context ctx, CodeParser.ClosureFunction func,
			BurningGeneratorTemplate machine) {
		machine.postTickListeners.add((world, pos, compound) -> func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static BurningGeneratorTemplate register(BurningGeneratorTemplate machine) {
		MachineRegistry.add(machine);
		ItemRegistry.addItem(machine.getRegistryItem());
		return machine;
	}
}
