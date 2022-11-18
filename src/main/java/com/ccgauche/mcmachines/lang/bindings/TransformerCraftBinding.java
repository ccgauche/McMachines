package com.ccgauche.mcmachines.lang.bindings;

import java.util.List;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.Dual;
import com.ccgauche.mcmachines.json.recipe.TransformerCraft;
import com.ccgauche.mcmachines.lang.CodeParser;
import com.ccgauche.mcmachines.lang.Context;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;
import com.ccgauche.mcmachines.machine.implementations.SimpleTransformerTemplate;

import net.minecraft.item.ItemStack;

@TypeAlias(name = "TransformerCraft")
public class TransformerCraftBinding {

	public static TransformerCraft $new(int energy, List<Dual<CItem, Integer>> input,
			List<Dual<ItemStack, Integer>> output) {
		return new TransformerCraft(energy, input, output);
	}

	@Method
	public static TransformerCraft onPreCraft(Context ctx, CodeParser.ClosureFunction func, TransformerCraft machine) {
		machine.preCraft.add((world, pos, compound) -> (boolean) func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static TransformerCraft onPostCraft(Context ctx, CodeParser.ClosureFunction func, TransformerCraft machine) {
		machine.postCraft.add((world, pos, compound) -> func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static TransformerCraft register(SimpleTransformerTemplate template, TransformerCraft machine) {
		template.bindCraftModel(machine);
		return machine;
	}
}
