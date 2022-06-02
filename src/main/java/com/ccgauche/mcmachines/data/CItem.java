package com.ccgauche.mcmachines.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CItem {

    @Nullable
    private final Item vanilla;
    @Nullable
    private final String id;

    public CItem(@NotNull Item vanilla) {
        this.vanilla = vanilla;
        this.id = null;
    }

    public CItem(@NotNull String id) {
        this.vanilla = null;
        this.id = id;
    }

    public CItem(@NotNull ItemStack itemStack) {
        NbtCompound compound = itemStack.getNbt();
        if (compound != null && compound.contains("c_item_id")) {
            this.id = compound.getString("c_item_id");
            this.vanilla = null;
        } else {
            this.id = null;
            this.vanilla = itemStack.getItem();
        }
    }


    public @Nullable Item getVanilla() {
        return vanilla;
    }

    /**
     * @return This will try to return the Item material even if the item is custom (Will be the material it's based on
     * If null this means the item doesn't exist
     */
    public @Nullable Item getItem() {
        if (vanilla != null) {
            return vanilla;
        } else {
            if (id == null) return null;
            IItem item = ItemRegistry.getItem(id);
            if (item == null) return null;
            return item.material();
        }
    }

    public @Nullable String getCustom() {
        return id;
    }

    public boolean isCustom() {
        return id != null;
    }

    @NotNull
    public ItemStack asStack(int amount, @Nullable DataCompound map) {
        if (id != null) {
            return Objects.requireNonNull(ItemRegistry.getItem(id)).create(amount, map);
        } else {
            return new ItemStack(vanilla);
        }
    }

    @NotNull
    public ItemStack asStack(int amount) {
        return asStack(amount, null);
    }

    @Override
    public String toString() {
        if (vanilla == Items.AIR) {
            return "<empty>";
        }
        if (id == null) {
            return vanilla.toString();
        } else {
            return id;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CItem cItem = (CItem) o;
        return Objects.equals(vanilla, cItem.vanilla) && Objects.equals(id, cItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vanilla, id);
    }
}
