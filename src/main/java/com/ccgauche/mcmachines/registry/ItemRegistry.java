package com.ccgauche.mcmachines.registry;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.handler.Registry;

public class ItemRegistry {

	@NotNull
	private static final HashMap<@NotNull String, @NotNull IItem> items = new HashMap<>();

	@NotNull
	public static HashMap<@NotNull String, @NotNull IItem> getItems() {
		return items;
	}

	public static void addItem(@NotNull IItem item) {
		items.put(item.id(), item);
		Registry.addListeners(item);
	}

	@Nullable
	public static IItem getItem(@NotNull String id) {
		return items.get(id);
	}

}
