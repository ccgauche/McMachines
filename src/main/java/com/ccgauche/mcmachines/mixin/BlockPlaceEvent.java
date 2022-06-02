package com.ccgauche.mcmachines.mixin;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.internals.Clock;
import com.ccgauche.mcmachines.machine.IMachine;
import com.ccgauche.mcmachines.utils.TextUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin (BlockItem.class)
public class BlockPlaceEvent{

	@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("TAIL"))
	private void restrict(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		this.restrict(context, state);
	}


	/**
	 * @param context the context of the placement
	 * @param state the state to place
	 */
	@Unique
	public void restrict(@NotNull ItemPlacementContext context, BlockState state) {
		ItemStack stack = Objects.requireNonNull(context.getPlayer()).getStackInHand(context.getHand());
		CItem item = new CItem(stack);
		if (item.isCustom()) {
			IMachine machine = Clock.get(item.getCustom());
			if (machine == null) {
				context.getPlayer().sendMessage(TextUtils.from("§cCan't place machine with invalid ID"), false);
			} else {
				machine.place(context.getWorld(), context.getBlockPos(), new DataCompound());
				context.getPlayer().sendMessage(TextUtils.from("§aMachine placed"), false);
			}
		}
	}
}