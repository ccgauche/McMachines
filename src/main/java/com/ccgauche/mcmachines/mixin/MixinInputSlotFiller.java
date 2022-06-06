package com.ccgauche.mcmachines.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.ccgauche.mcmachines.data.CItem;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.InputSlotFiller;

@Mixin(InputSlotFiller.class)
public abstract class MixinInputSlotFiller {

	@Redirect(method = "fillInputSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;indexOf(Lnet/minecraft/item/ItemStack;)I"))
	private int playerInventoryFindStack(PlayerInventory inventory, ItemStack stack) {
		CItem item = new CItem(stack);
		for (int i = 0; i < inventory.main.size(); i++) {
			ItemStack stack2 = inventory.main.get(i);
			if (stack.getItem() == stack2.getItem() && item.equals(new CItem(stack2)))
				return i;
		}
		return -1;
	}
}