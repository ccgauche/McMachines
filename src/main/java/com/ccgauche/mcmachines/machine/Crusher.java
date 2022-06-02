package com.ccgauche.mcmachines.machine;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.GlobalKeys;
import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.json.recipe.TransformerCraft;
import com.ccgauche.mcmachines.utils.ItemUtils;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Crusher implements IMachine, ICraftingMachine {

    private final List<TransformerCraft> crafts = new ArrayList<>();

    @Override
    public String id() {
        return "base:crusher";
    }

    @Override
    public void place(World world, BlockPos pos, DataCompound properties) {
        IMachine.super.place(world, pos, properties);
        GlobalKeys.ENERGY_MAX.set(pos, 10_000);
        GlobalKeys.ENERGY_IN.set(pos, 1_00);
    }

    @Override
    public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
        DropperBlockEntity furnaceBlock = (DropperBlockEntity) world.getBlockEntity(pos);
        if (furnaceBlock == null) return;
        int k = GlobalKeys.ENERGY_CONTENT.getOrDefault(object, 0);
        a: for (int i = 0; i < 9; i++) {
            ItemStack item =
                    furnaceBlock.getStack(i);
            if (item == null || item.getItem() == null || item.isEmpty()) continue;

            CItem citem = new CItem(item);

            for (TransformerCraft craft : crafts) {
                if (k < craft.energy_use) {
                    continue;
                }
                if (citem.equals(craft.input_type) && item.getCount() >= craft.input_amount) {
                    ItemUtils.removeItem(furnaceBlock, i, craft.input_amount);
                    GlobalKeys.ENERGY_CONTENT.set(object, k - craft.energy_use);
                    for (var toDrop : craft.outputs) {
                        ItemStack stack = toDrop.getFirst();
                        int multiplier = toDrop.getSecond() / 100 + ((new Random().nextInt(100) <= toDrop.getSecond()%100) ? 1 : 0);
                        stack.setCount(stack.getCount() * multiplier);
                        if (stack.getCount() == 0) {
                            continue;
                        }
                        ItemUtils.dispense((ServerWorld) world, pos, stack);
                    }
                    break a;
                }
            }
            break;
        }

    }

    @Override
    public Class<? extends IRecipe> getCraftModelType() {
        return TransformerCraft.class;
    }

    @Override
    public void bindCraftModel(@NotNull IRecipe recipe) {
        if (recipe instanceof TransformerCraft craft) {
            crafts.add(craft);
        } else {
            System.out.println("Invalid recipe type "+recipe.getClass().getSimpleName());
        }
    }

    @Override
    public String getCraftModelRegistryKey() {
        return id();
    }
}
