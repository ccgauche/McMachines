package com.ccgauche.mcmachines.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ccgauche.mcmachines.registry.DataRegistry;

import net.minecraft.block.DropperBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(DropperBlock.class)
public class DropperBlockMixin {

	@Inject(method = "dispense", at = @At("HEAD"), cancellable = true)
	public void dispense(ServerWorld world, BlockPos pos, CallbackInfo ci) {
		if (DataRegistry.ID.get(world, pos) != null) {
			ci.cancel();
		}
	}
}
