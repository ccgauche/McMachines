package com.ccgauche.mcmachines.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.machine.CraftingTableCraftProvider;
import com.ccgauche.mcmachines.machine.ICraftingMachine;
import com.google.common.collect.ImmutableMap;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class CraftRegistry {
	public static final List<Recipe<?>> recipes = new ArrayList<>();
	@NotNull
	private final static HashMap<String, ICraftingMachine> crafts = new HashMap<>();

	public static HashMap<String, ICraftingMachine> get() {
		return crafts;
	}

	@Nullable
	@Contract("null -> null")
	public static ICraftingMachine get(@Nullable String string) {
		return crafts.get(string);
	}

	public static void add(@NotNull ICraftingMachine machine) {
		crafts.put(machine.getCraftModelRegistryKey(), machine);
	}

	public static void registerCrafts(Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2) {
		CraftingTableCraftProvider iCraftingMachine = (CraftingTableCraftProvider) crafts
				.get("minecraft:crafting_table");
		iCraftingMachine.register(map2);
	}

	static {
		add(new CraftingTableCraftProvider());
	}
}
