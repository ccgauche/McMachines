package com.ccgauche.mcmachines.data;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public final class GlobalKeys {

    @NotNull
    private static final HashMap<@NotNull BlockPos, DataCompound> map = new HashMap<>();

    public static HashMap<@NotNull BlockPos, DataCompound> entries() {
        return map;
    }

    public static void write(@NotNull BlockPos pos, DataCompound compound) {
        DataCompound compound1 = map.get(pos);
        if (compound1 == null) {
            map.put(pos, compound);
        } else {
            compound1.merge(compound);
        }
    }

    public static void setRaw(@NotNull String key, @NotNull BlockPos pos, @Nullable Object value) {
        @NotNull
        var stringObjectHashMap = map.get(pos);
        if (stringObjectHashMap == null) {
            if (value == null)
                return;
            DataCompound tmp = new DataCompound();
            tmp.set(key, value);
            map.put(pos, tmp);
        } else {
            stringObjectHashMap.set(key, value);
        }
    }

    @Nullable
    public static Object getRaw(@NotNull String key, @NotNull BlockPos pos) {
        var stringObjectHashMap = map.get(pos);
        if (stringObjectHashMap == null)
            return null;
        return stringObjectHashMap.get(key);
    }


    public static final Key<Integer> ENERGY_CONTENT = new Key<>("energy_content");
    public static final Key<Integer> ENERGY_MAX = new Key<>("energy_max");
    public static final Key<Integer> ENERGY_OUT = new Key<>("energy_out");
    public static final Key<Integer> ENERGY_IN = new Key<>("energy_in");
    public static final Key<Integer> HOLD_RESISTANCE = new Key<>("hold_resistance");
    public static final Key<String> ID = new Key<>("id");
    public static final Key<Integer> CHARGE_PER_TICK = new Key<>("charge_per_tick");
    public static final Key<Integer> GEN_PER_TICK = new Key<>("gen_per_second");
    public static final Key<Integer> GEN_REMAINING_SECS = new Key<>("gen_remaining_secs");

    public static final class Key<T> {
        @NotNull
        private final String key;

        public Key(@NotNull String k) {
            this.key = k;
        }

        @NotNull
        public T getOrDefault(@NotNull DataCompound state, @NotNull T t) {
            return Optional.ofNullable(get(state)).orElse(t);
        }

        @Nullable
        public T get(@NotNull DataCompound state) {
            return (T) state.get(key);
        }

        public void set(@NotNull DataCompound state, @Nullable T value) {
            state.set(key, value);
        }

        @NotNull
        public String getKey() {
            return key;
        }

        public void set(@NotNull BlockPos pos, @Nullable T value) {
            setRaw(key, pos, value);
        }

        @NotNull
        public T getOrDefault(@NotNull BlockPos pos, @NotNull T t) {
            return Optional.ofNullable(get(pos)).orElse(t);
        }

        @Nullable
        public T get(@NotNull BlockPos pos) {
            return (T) getRaw(key, pos);
        }
    }

}
