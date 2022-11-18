package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.json.recipe.TransformerCraft;
import com.ccgauche.mcmachines.lang.CodeParser;
import com.ccgauche.mcmachines.lang.Context;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;
import com.ccgauche.mcmachines.machine.implementations.SimpleTransformerTemplate;
import com.ccgauche.mcmachines.registry.ItemRegistry;
import com.ccgauche.mcmachines.registry.MachineRegistry;

import net.minecraft.item.Items;

@TypeAlias(name = "Transformer")
public class TransformerBinding {

	public static SimpleTransformerTemplate $new(String id, String name, DataCompound properties) {
		return new SimpleTransformerTemplate(new CItem(Items.DROPPER), id, name, properties);
	}

	@Method
	public static SimpleTransformerTemplate addRecipe(TransformerCraft craft, SimpleTransformerTemplate machine) {
		machine.bindCraftModel(craft);
		return machine;
	}

	@Method
	public static SimpleTransformerTemplate onPreTick(Context ctx, CodeParser.ClosureFunction func,
			SimpleTransformerTemplate machine) {
		machine.preTickListeners
				.add((world, pos, compound) -> (boolean) func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static SimpleTransformerTemplate onPostTick(Context ctx, CodeParser.ClosureFunction func,
			SimpleTransformerTemplate machine) {
		machine.postTickListeners.add((world, pos, compound) -> func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static SimpleTransformerTemplate register(SimpleTransformerTemplate machine) {
		MachineRegistry.add(machine);
		ItemRegistry.addItem(machine.getRegistryItem());
		return machine;
	}
}
