package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;

import net.minecraft.item.ItemStack;

@TypeAlias(name = "ItemStack")
public class ItemStackBinding {

	public static ItemStack $new(CItem item) {
		return item.asStack(1);
	}

	@Method
	public static ItemStack setAmount(int amount, ItemStack stack) {
		stack.setCount(amount);
		return stack;
	}
}
