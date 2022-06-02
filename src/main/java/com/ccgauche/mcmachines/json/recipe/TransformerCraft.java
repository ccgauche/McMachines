package com.ccgauche.mcmachines.json.recipe;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.Dual;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TransformerCraft implements IRecipe{

    @NotNull
    public String on;
    public Integer energy_use;
    @NotNull
    public CItem input_type;
    public Integer input_amount;
    @NotNull
    public List<@NotNull Dual<@NotNull ItemStack, @NotNull Integer>> outputs;


    @Override
    public String on() {
        return on;
    }
}
