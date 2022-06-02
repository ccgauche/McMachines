package com.ccgauche.mcmachines.machine.implementations;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.utils.ItemUtils;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.GlobalKeys;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.conditions.ICondition;
import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.json.recipe.TransformerCraft;
import com.ccgauche.mcmachines.machine.ICraftingMachine;
import com.ccgauche.mcmachines.machine.IMachine;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleTransformerTemplate implements IMachine, ICraftingMachine {

    private final List<TransformerCraft> crafts = new ArrayList<>();

    private final @NotNull String id;
    private final @NotNull String name;
    private final @Nullable DataCompound properties;
    private final @Nullable ICondition conditions;

    private final IItem item;

    public SimpleTransformerTemplate(@NotNull String id, @NotNull String name,
                                     @Nullable DataCompound properties,
                                     @Nullable ICondition conditions) {
        this.id = id;
        this.name = name;
        this.properties = properties;
        this.conditions = conditions;
        item = new IItem.Basic(Items.DROPPER, this.name, this.id, this.properties, List.of());
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void place(World world, BlockPos pos, DataCompound properties) {
        IMachine.super.place(world, pos, properties);
        GlobalKeys.write(pos, this.properties);
        GlobalKeys.write(pos, properties);
    }

    @Override
    public void tick(@NotNull DataCompound object, World world, BlockPos pos) {
        DropperBlockEntity furnaceBlock = (DropperBlockEntity) world.getBlockEntity(pos);
        if (furnaceBlock == null) return;
        int k = GlobalKeys.ENERGY_CONTENT.getOrDefault(object, 0);
        a:
        for (int i = 0; i < 9; i++) {
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
                        ItemStack stack = toDrop.getFirst().copy();
                        int multiplier = toDrop.getSecond() / 100 + ((new Random().nextInt(100) <= toDrop.getSecond() % 100) ? 1 : 0);
                        System.out.println("Multiplier "+multiplier+" "+stack);
                        stack.setCount(stack.getCount() * multiplier);
                        if (stack.getCount() == 0) {
                            continue;
                        }
                        System.out.println("stack "+stack);
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
            System.out.println("Invalid recipe type " + recipe.getClass().getSimpleName());
        }
    }

    @Override
    public String getCraftModelRegistryKey() {
        return id();
    }

    public IItem getRegistryItem() {
        return item;
    }
}
