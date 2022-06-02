package com.ccgauche.mcmachines.mixin;

import com.ccgauche.mcmachines.data.CItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Ingredient.class)
public class ItemStackMixin {

    @Shadow
    private void cacheMatchingStacks() {
    }

    @Shadow
    private ItemStack[] matchingStacks;

    @Inject(method = "test(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void breakBlock(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (itemStack == null) {
            cir.setReturnValue(false);
        } else {
            this.cacheMatchingStacks();
            if (this.matchingStacks.length == 0) {
                cir.setReturnValue(itemStack.isEmpty());
            } else {
                CItem item1 = new CItem(itemStack);
                for (ItemStack itemStack2 : this.matchingStacks) {
                    if (itemStack2.isOf(itemStack.getItem()) && new CItem(itemStack2).equals(item1)) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
                cir.setReturnValue(false);
            }
        }

    }
    /*@Inject(method = "matches(Lnet/minecraft/inventory/CraftingInventory;Lnet/minecraft/world/World;)Z",
            at = @At("HEAD"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true)
    private void breakBlock(CraftingInventory craftingInventory, World world, CallbackInfoReturnable<Boolean> cir) {

        for(int i = 0; i <= craftingInventory.getWidth(); ++i) {
            for(int j = 0; j <= craftingInventory.getHeight(); ++j) {
                int slot = i + j * craftingInventory.getWidth();
                ItemStack stack = craftingInventory.getStack(slot);
                if (stack == null) continue;
                if (new CItem(stack).isCustom()) {
                    cir.setReturnValue(false);
                    cir.cancel();
                    return;
                }
            }
        }
    }*/
}
