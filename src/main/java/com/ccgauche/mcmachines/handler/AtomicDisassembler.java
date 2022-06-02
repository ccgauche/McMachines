package com.ccgauche.mcmachines.handler;

import com.ccgauche.mcmachines.data.GlobalKeys;
import com.ccgauche.mcmachines.utils.TextUtils;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.handler.events.BlockInteractListener;

public class AtomicDisassembler implements BlockInteractListener {
    @Override
    public boolean onInteract(BlockInteract blockInteract) {
        DataCompound compound = new DataCompound(blockInteract.stack());
        int hardness = (int) Math.ceil(blockInteract.getBlockState().getHardness(null, null));
        if (hardness < 0 || hardness > 24000) {
            blockInteract.player().sendMessage(TextUtils.from("§cCan't break this block type"), true);
            return false;
        }
        hardness = hardness * hardness;
        int energy = GlobalKeys.ENERGY_CONTENT.getOrDefault(compound,0);
        if (energy < hardness) {
            blockInteract.player().sendMessage(TextUtils.from("§cNot enough energy"), true);
            return false;
        }
        GlobalKeys.ENERGY_CONTENT.set(compound, energy - hardness);
        compound.updateStack(blockInteract.stack());
        blockInteract.world().breakBlock(blockInteract.blockHitResult().getBlockPos(),true, blockInteract.player());
        return true;
    }

    @Override
    public String handlerId() {
        return "atomicDisassembler";
    }
}
