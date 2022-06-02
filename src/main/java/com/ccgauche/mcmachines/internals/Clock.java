package com.ccgauche.mcmachines.internals;

import com.ccgauche.mcmachines.data.GlobalKeys;
import com.ccgauche.mcmachines.machine.CraftingTableCraftProvider;
import com.ccgauche.mcmachines.machine.ICraftingMachine;
import com.ccgauche.mcmachines.machine.IMachine;
import com.google.common.collect.ImmutableMap;
import com.ccgauche.mcmachines.data.DataCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Clock {

    @NotNull
    private final static HashMap<String, IMachine> map = new HashMap<>();
    @NotNull
    private final static HashMap<String, ICraftingMachine> crafts = new HashMap<>();

    static {
        addCraftRegistry(new CraftingTableCraftProvider());
    }

    public static void registerCrafts(Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2) {
        CraftingTableCraftProvider iCraftingMachine = (CraftingTableCraftProvider) crafts.get("minecraft:crafting_table");
        iCraftingMachine.register(map2);
    }

    @NotNull
    public static HashMap<String, IMachine> getMap() {
        return map;
    }

    @NotNull
    public static Map<String, ICraftingMachine> getCrafts() {
        return crafts;
    }

    @Nullable
    public static IMachine get(@Nullable String machine) {
        return map.get(machine);
    }

    public static void add(@NotNull IMachine machine) {
        map.put(machine.id(), machine);
        if (machine instanceof ICraftingMachine m) {
            addCraftRegistry(m);
        }
    }

    public static void addCraftRegistry(@NotNull ICraftingMachine machine) {
        crafts.put(machine.getCraftModelRegistryKey(), machine);
    }

    @SuppressWarnings("all")
    public static void tick(@NotNull World world) {
        for (var pos : ((HashMap<@NotNull BlockPos, DataCompound>)(GlobalKeys.entries().clone())).entrySet()) {
            String orDefault = GlobalKeys.ID.getOrDefault(pos.getValue(), "");
            IMachine machine = map.get(orDefault);
            if (machine != null) {
                machine.tick(pos.getValue(), world, pos.getKey());
            }
        }
    }
}
