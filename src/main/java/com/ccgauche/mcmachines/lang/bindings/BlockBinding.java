package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@TypeAlias(name = "Block")
public class BlockBinding {

	public static Block of(CItem item) {
		return Block.getBlockFromItem(item.getItemOrCrash());
	}

	public static Block ofString(String item) {
		return Registry.BLOCK.get(Identifier.tryParse(("//" + item).replace("//:", "minecraft:").replace("//", "")));
	}

	// getBlastResistance
	@Method
	public static float blastResistance(Block block) {
		return block.getBlastResistance();
	}

	// getSlipperiness
	@Method
	public static float slipperiness(Block block) {
		return block.getSlipperiness();
	}

	// getHardness
	@Method
	public static float hardness(Block block) {
		return block.getHardness();
	}

}
