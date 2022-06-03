package com.ccgauche.mcmachines.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.handler.Registry;
import com.ccgauche.mcmachines.internals.Clock;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Mixin(World.class)
public class WorldEvent {

	private static boolean craftAdded = false;

	@Inject(method = "tickBlockEntities", at = @At("TAIL"))
	private void tickBlockEntities(CallbackInfo ci) {
		if (!craftAdded) {
			craftAdded = true;
		}
		ServerWorld o = (ServerWorld) (Object) (this);
		Clock.tick(o);
		for (ServerPlayerEntity player : o.getPlayers()) {
			applyStack(player.getMainHandStack(), player, o);
			applyStack(player.getOffHandStack(), player, o);
			for (ItemStack item : player.getArmorItems()) {
				applyStack(item, player, o);
			}
		}
	}

	private static void applyStack(ItemStack current, ServerPlayerEntity player, ServerWorld o) {
		CItem citem = new CItem(current);
		if (citem.isCustom()) {
			var handler = Registry.playerTick.get(citem.getCustom());
			if (handler != null) {
				for (var k : handler) {
					k.onTick(player, o, current);
				}
			}
		}
	}
}
