package com.ccgauche.mcmachines.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ccgauche.mcmachines.internals.Clock;

import net.minecraft.world.World;

@Mixin(World.class)
public class WorldEvent {

	private static boolean craftAdded = false;

	@Inject(method = "tickBlockEntities", at = @At("TAIL"))
	private void tickBlockEntities(CallbackInfo ci) {
		if (!craftAdded) {
			craftAdded = true;
		}
		Object o = this;
		Clock.tick((World) o);
	}
}
