package com.ccgauche.mcmachines.machine;

import com.ccgauche.mcmachines.data.CItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class SimpleMachineCraft {

    private final CItem inItem;
    private final int inItemAmount;
    private final int energyUse;
    private final Map<ItemStack, Integer> drops;


    public SimpleMachineCraft(CItem inItem, int inItemAmount, int energyUse, Map<ItemStack, Integer> drops) {
        this.inItem = inItem;
        this.inItemAmount = inItemAmount;
        this.energyUse = energyUse;
        this.drops = drops;
    }

    public SimpleMachineCraft(Item inItem, int inItemAmount, int energyUse, Map<ItemStack, Integer> drops) {
        this.inItem = new CItem(inItem);
        this.inItemAmount = inItemAmount;
        this.energyUse = energyUse;
        this.drops = drops;
    }

    public SimpleMachineCraft(Item inItem, int inItemAmount, int energyUse) {
        this.inItem = new CItem(inItem);
        this.inItemAmount = inItemAmount;
        this.energyUse = energyUse;
        this.drops = new HashMap<>();
    }

    public SimpleMachineCraft(CItem inItem, int inItemAmount, int energyUse) {
        this.inItem = inItem;
        this.inItemAmount = inItemAmount;
        this.energyUse = energyUse;
        this.drops = new HashMap<>();
    }

    public SimpleMachineCraft addOut(ItemStack stack, int perc) {
        drops.put(stack, perc);
        return this;
    }

    public int getEnergyUse() {
        return energyUse;
    }

    public Map<ItemStack, Integer> getDrops() {
        return drops;
    }

    public List<ItemStack> outs() {
        List<ItemStack> list = new ArrayList<>();
        for (Map.Entry<ItemStack,Integer> l : drops.entrySet()) {
            ItemStack stack = l.getKey().copy();
            int amount = stack.getCount() * (l.getValue() / 100) + (stack.getCount()) * (new Random().nextInt(100) < l.getValue() ? 1 : 0);
            while (amount > 0) {
                ItemStack stack1 = l.getKey().copy();
                stack1.setCount(Math.min(amount,stack.getMaxCount()));
                amount -= stack1.getCount();
                list.add(stack1);
            }
        }
        return list;
    }

    public CItem getInItem() {
        return inItem;
    }

    public int getInItemAmount() {
        return inItemAmount;
    }
}
