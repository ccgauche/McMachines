package com.ccgauche.mcmachines.mixin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.handler.Registry;
import com.ccgauche.mcmachines.internals.Clock;
import com.ccgauche.mcmachines.registry.CraftRegistry;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Mixin(World.class)
public class WorldEvent {

	private static boolean craftAdded = false;

	private final Set<UUID> craftSent = new HashSet<>();

	@Inject(method = "tickBlockEntities", at = @At("TAIL"))
	private void tickBlockEntities(CallbackInfo ci) {
		if (!craftAdded) {
			craftAdded = true;
		}
		ServerWorld o = (ServerWorld) (Object) (this);
		Clock.tick(o);
		for (ServerPlayerEntity player : o.getPlayers()) {
			UUID k = player.getUuid();
			if (!craftSent.contains(k)) {
				craftSent.add(k);
				player.unlockRecipes(CraftRegistry.recipes.toArray(Identifier[]::new));
			}
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
