package com.ccgauche.mcmachines.lang.bindings;

import java.util.List;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.Dual;
import com.ccgauche.mcmachines.json.recipe.GeneratorCraft;
import com.ccgauche.mcmachines.lang.CodeParser;
import com.ccgauche.mcmachines.lang.Context;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;
import com.ccgauche.mcmachines.machine.implementations.SimpleTransformerTemplate;

import net.minecraft.item.ItemStack;

@TypeAlias(name = "GeneratorCraft")
public class GeneratorCraftBinding {

	public static GeneratorCraft $new(List<Dual<CItem, Integer>> input, List<Dual<ItemStack, Integer>> output,
			int production, int duration) {
		return new GeneratorCraft(input, output, production, duration);
	}

	@Method
	public static GeneratorCraft onPreCraft(Context ctx, CodeParser.ClosureFunction func, GeneratorCraft machine) {
		machine.preCraft.add((world, pos, compound) -> (boolean) func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static GeneratorCraft onPostCraft(Context ctx, CodeParser.ClosureFunction func, GeneratorCraft machine) {
		machine.postCraft.add((world, pos, compound) -> func.run(ctx, new Object[] { world, pos, compound }));
		return machine;
	}

	@Method
	public static GeneratorCraft register(SimpleTransformerTemplate template, GeneratorCraft machine) {
		template.bindCraftModel(machine);
		return machine;
	}
}
