package com.ccgauche.mcmachines.registry;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.handler.Registry;

/**
 * The registry in which all items are stored
 */
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

	@NotNull
	public static IItem getItemOrCrash(@NotNull String id) {
		var h = getItem(id);
		if (h == null) {
			throw new RuntimeException("Invalid ID " + id + " not found");
		}
		return h;
	}

	@Nullable
	public static IItem getItem(@NotNull String id) {
		return items.get(id);
	}

}
