package com.ccgauche.mcmachines.mixin;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.GlobalKeys;
import com.ccgauche.mcmachines.utils.TextUtils;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.handler.Registry;
import com.ccgauche.mcmachines.handler.events.BlockInteractListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteraction {


    @Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
    public void interactBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> info) {


        if (stack.getItem() != Items.AIR) {
            CItem citem = new CItem(stack);
            if (citem.isCustom()) {
                var handler = Registry.blockInteract.get(citem.getCustom());
                if (handler != null) {
                    boolean rs = false;
                    for (var k : handler) {
                        rs = rs || k.onInteract(new BlockInteractListener.BlockInteract(player,world,stack,hand,blockHitResult));
                    }
                    if (rs) {
                        info.cancel();
                        info.setReturnValue(ActionResult.FAIL);
                        return;
                    }
                }
            }
        }
        if (stack.getItem() != Items.BLAZE_ROD) {
            return;
        }

        info.cancel();
        info.setReturnValue(ActionResult.FAIL);

        BlockPos pos = blockHitResult.getBlockPos();
        DataCompound stringObjectHashMap = GlobalKeys.entries().get(pos);
        if (stringObjectHashMap == null) {
            player.sendMessage(TextUtils.from("§cNo block data here"), false);
            return;
        }


        for (Map.Entry<String, Object> o : stringObjectHashMap.getMap().entrySet()) {
            String type, content;
            if (o.getValue() instanceof String c) {
                type = "String";
                content = c;
            } else if (o.getValue() instanceof Integer c) {
                type = "Int";
                content = c + "";
            } else {
                type = o.getValue().getClass().getSimpleName();
                content = o.getValue().toString();
            }
            player.sendMessage(TextUtils.from("§7(§a" + type + "§7) §e" + o.getKey() + "§6 = §f" + content), false);
        }
    }

    @Shadow
    protected ServerWorld world;

    @Inject(method = "tryBreakBlock",
            at = @At("TAIL"))
    private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            GlobalKeys.entries().remove(pos);
        }
    }
}
