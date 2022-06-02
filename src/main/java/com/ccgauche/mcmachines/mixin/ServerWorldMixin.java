package com.ccgauche.mcmachines.mixin;

import java.io.File;
import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ccgauche.mcmachines.registry.DataRegistry;

import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

	@Inject(method = "saveLevel", at = @At("HEAD"))
	public void saveLevel(CallbackInfo ci) {
		try {
			DataRegistry.save(new File("machines.dat"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
