package com.ccgauche.mcmachines.mixin;

import com.ccgauche.mcmachines.internals.Clock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldEvent {

    private static boolean craftAdded = false;

    @Inject(method = "tickBlockEntities", at = @At("TAIL"))
    private void tickBlockEntities(CallbackInfo ci) {
        if (!craftAdded) {
            craftAdded = true;
            ServerWorld w = (ServerWorld) (Object) this;
            //w.getRecipeManager().listAllOfType(RecipeType.CRAFTING).add();

        }
        Object o = this;
        Clock.tick((World) o);
    }
}
