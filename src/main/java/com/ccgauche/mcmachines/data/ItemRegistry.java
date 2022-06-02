package com.ccgauche.mcmachines.data;

import com.ccgauche.mcmachines.handler.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

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

    static {
        /* addItem(new IItem.Basic(Items.CHARCOAL, "§7Graphite", "base:graphite"));
        addItem(new IItem.Basic(Items.DROPPER, "§7Crusher", "base:crusher"));
        addItem(new IItem.Basic(Items.DROPPER, "§7Generator", "base:generator")); */
    }
}
