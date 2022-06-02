package com.ccgauche.mcmachines.machine.implementations;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.GlobalKeys;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.conditions.ICondition;
import com.ccgauche.mcmachines.machine.IMachine;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleChargerTemplate implements IMachine {

    private final @NotNull String id;
    private final @NotNull String name;
    private final @Nullable DataCompound properties;
    private final @Nullable ICondition conditions;

    private final IItem item;

    public SimpleChargerTemplate(@NotNull String id, @NotNull String name,
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
        int e = GlobalKeys.ENERGY_CONTENT.getOrDefault(object, 0);
        int k = GlobalKeys.CHARGE_PER_TICK.getOrDefault(object, 0);
        if (k == 0 || e == 0)
            return;
        for (int i = 0; i < 9; i++) {
            ItemStack item =
                    furnaceBlock.getStack(i);
            if (item == null || item.getItem() == null || item.isEmpty()) continue;

            DataCompound compound = new DataCompound(item);
            int maxE = GlobalKeys.ENERGY_MAX.getOrDefault(compound, 0);
            if (maxE == 0 || k <= 0) {
                continue;
            }
            int ec = GlobalKeys.ENERGY_CONTENT.getOrDefault(compound, 0);
            if (ec >= maxE) {
                continue;
            }
            int maxToAdd = Math.min(e,Math.min((maxE - ec), k));
            k -= maxToAdd;
            e -= maxToAdd;
            GlobalKeys.ENERGY_CONTENT.set(compound, ec + maxToAdd);
            compound.updateStack(item);
            if (k == 0 || e == 0)
                break;
        }
        GlobalKeys.ENERGY_CONTENT.set(object, e);

    }

    public IItem getRegistryItem() {
        return item;
    }
}
