package com.ccgauche.mcmachines.machine;

import com.ccgauche.mcmachines.json.recipe.IRecipe;
import org.jetbrains.annotations.NotNull;

public interface ICraftingMachine {

    Class<? extends IRecipe> getCraftModelType();
    void bindCraftModel(@NotNull IRecipe recipe);
    String getCraftModelRegistryKey();
}
