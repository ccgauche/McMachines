package com.ccgauche.mcmachines.registry;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.internals.Encoder;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class DataRegistry {

	@NotNull
	private static HashMap<Identifier, HashMap<@NotNull BlockPos, DataCompound>> map = new HashMap<>();

	public static HashMap<Identifier, HashMap<@NotNull BlockPos, DataCompound>> entries() {
		return map;
	}

	public static HashMap<@NotNull BlockPos, DataCompound> getMap(World world) {
		return getMap(world.getRegistryKey().getValue());
	}

	public static HashMap<@NotNull BlockPos, DataCompound> getMap(Identifier identifier) {
		var g = map.get(identifier);
		if (g == null) {
			var je = new HashMap<@NotNull BlockPos, DataCompound>();
			map.put(identifier, je);
			return je;
		}
		return g;
	}

	public static void save(File file) throws IOException {
		if (file.exists())
			file.delete();
		file.createNewFile();
		OutputStream stream = new FileOutputStream(file);
		Encoder.encodeWorldRegistry(map, stream);
		stream.close();
	}

	public static void load(File file) throws IOException {
		if (!file.exists())
			return;
		InputStream stream = new FileInputStream(file);
		map = Encoder.decodeWorldRegistry(stream);
		stream.close();
	}

	public static void write(@NotNull World world, @NotNull BlockPos pos, DataCompound compound) {
		var map = getMap(world);
		DataCompound compound1 = map.get(pos);
		if (compound1 == null) {
			map.put(pos, compound);
		} else {
			compound1.merge(compound);
		}
	}

	public static void setRaw(@NotNull World world, @NotNull String key, @NotNull BlockPos pos,
			@Nullable Object value) {
		var map = getMap(world);
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
	public static Object getRaw(@NotNull World world, @NotNull String key, @NotNull BlockPos pos) {
		var map = getMap(world);
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

		public void set(@NotNull World world, @NotNull BlockPos pos, @Nullable T value) {
			setRaw(world, key, pos, value);
		}

		@NotNull
		public T getOrDefault(@NotNull World world, @NotNull BlockPos pos, @NotNull T t) {
			return Optional.ofNullable(get(world, pos)).orElse(t);
		}

		@Nullable
		public T get(@NotNull World world, @NotNull BlockPos pos) {
			return (T) getRaw(world, key, pos);
		}
	}

}
