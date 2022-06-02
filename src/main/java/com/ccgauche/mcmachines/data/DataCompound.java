package com.ccgauche.mcmachines.data;

import com.ccgauche.mcmachines.utils.ItemUtils;
import com.ccgauche.mcmachines.utils.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataCompound {

    @NotNull
    private final HashMap<@NotNull String, @NotNull Object> object;

    public DataCompound() {
        this(new HashMap<>());
    }

    public DataCompound(@NotNull ItemStack stack) {
        var compound = stack.getNbt();
        this.object = new HashMap<>();
        if (compound != null) {
            var compound1 = compound.getCompound("c_attr");
            if (compound1 != null) {
                for (String string : compound1.getKeys()) {
                    var g = compound1.get(string);
                    if (g instanceof NbtInt i) {
                        this.setInt(string, i.intValue());
                    } else if (g instanceof NbtString i) {
                        this.setString(string, i.asString());
                    }
                }
            }
        }
    }

    public void merge(DataCompound other) {
        for (Map.Entry<@NotNull String, @NotNull Object> e : other.object.entrySet()) {
            this.set(e.getKey(), e.getValue());
        }
    }

    public DataCompound(@NotNull HashMap<@NotNull String, @NotNull Object> object) {
        this.object = object;
    }


    public DataCompound clone() {
        return new DataCompound((HashMap<String, Object>) object.clone());
    }

    public @NotNull HashMap<@NotNull String, @NotNull Object> getMap() {
        return object;
    }

    @NotNull
    public DataCompound set(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            object.remove(key);
        } else {
            object.put(key, value);
        }
        return this;
    }

    @NotNull
    public DataCompound setString(@NotNull String key, @Nullable String value) {
        return set(key, value);
    }

    @NotNull
    public DataCompound setInt(@NotNull String key, @Nullable Integer value) {
        return set(key, value);
    }

    @Nullable
    public Object get(@NotNull String key) {
        return object.get(key);
    }

    @Nullable
    public String getInt(@NotNull String key) {
        return (String) object.get(key);
    }

    @Nullable
    public String getString(@NotNull String key) {
        return (String) object.get(key);
    }

    public void writeNbt(@NotNull NbtCompound compound) {
        compound.remove("c_attr");
        var compound1 = new NbtCompound();
        for (Map.Entry<String, Object> string : getMap().entrySet()) {
            if (string.getValue() instanceof Integer i) {
                compound1.putInt(string.getKey(), i);
            } else if (string.getValue() instanceof String i) {
                compound1.putString(string.getKey(), i);
            }
        }
        compound.put("c_attr", compound1);
        Object k = getMap().get("id");
        if (k != null)
            compound.putString("c_item_id", (String) k);
    }

    public void updateStack(@NotNull ItemStack stack) {
        List<Text> lore = new ArrayList<>();
        String id = GlobalKeys.ID.get(this);
        if (id != null) {
            lore.add(TextUtils.from("§r§7" + id));
        }
        Integer maxEnergy = GlobalKeys.ENERGY_MAX.get(this);
        if (maxEnergy != null) {
            lore.add(TextUtils.from("§r§fEnergy§6: §e" + GlobalKeys.ENERGY_CONTENT.getOrDefault(this, 0) + "§f/§e" + maxEnergy));
        }
        NbtCompound compound = stack.getOrCreateNbt();
        writeNbt(compound);
        stack.setNbt(compound);
        ItemUtils.setLore(stack, lore);
    }

    @Override
    public String toString() {
        return "DataCompound{" +
                "object=" + object +
                '}';
    }
}
