package com.ccgauche.mcmachines.mixin;

import com.ccgauche.mcmachines.internals.Clock;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.recipe.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerAccessor {

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci, Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2) {
        Clock.registerCrafts(map2);
    }
}
