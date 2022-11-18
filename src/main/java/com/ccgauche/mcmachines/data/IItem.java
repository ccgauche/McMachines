package com.ccgauche.mcmachines.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.handler.events.BlockInteractListener;
import com.ccgauche.mcmachines.handler.events.PlayerTickListener;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.utils.TextUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

/** An abstract item that can represent custom items and machines */
public interface IItem {

	@NotNull
	ItemStack create(int amount, @Nullable DataCompound map);

	@NotNull
	String id();

	@NotNull
	Item material();

	@NotNull
	List<BlockInteractListener> getInteractListeners();

	@NotNull
	List<PlayerTickListener> getTickListeners();

	static final class Basic
			implements IItem {
		private final @NotNull Item material;
		private final @NotNull String name;
		private final @NotNull String id;
		private final @Nullable DataCompound customProperties;
		private final @NotNull List<Attribute> attribute;

		private final List<BlockInteractListener> interactListeners = new ArrayList<>();
		private final List<PlayerTickListener> tickListeners = new ArrayList<>();

		public Basic(@NotNull Item material, @NotNull String name, @NotNull String id,
				@Nullable DataCompound customProperties, @NotNull List<Attribute> attribute) {
			this.material = material;
			this.name = name;
			this.id = id;
			this.customProperties = customProperties;
			this.attribute = attribute;
		}

		@Override
		@NotNull
		public ItemStack create(int amount, @Nullable DataCompound map) {
			ItemStack stack = new ItemStack(material);
			stack.setCount(amount);
			stack.setCustomName(TextUtils.from(name));
			DataCompound compound = customProperties == null ? new DataCompound() : customProperties.clone();
			if (map != null)
				compound.merge(map);
			DataRegistry.ID.set(compound, id);
			compound.updateStack(stack);
			NbtCompound compound1 = stack.getOrCreateNbt();
			NbtList attributes = compound1.getList("AttributeModifiers", NbtElement.COMPOUND_TYPE);
			attribute.forEach(e -> e.addTo(attributes));
			compound1.put("AttributeModifiers", attributes);
			stack.setNbt(compound1);
			return stack;
		}

		public @NotNull Item material() {
			return material;
		}

		public @NotNull String name() {
			return name;
		}

		public @NotNull String id() {
			return id;
		}

		public @Nullable DataCompound customProperties() {
			return customProperties;
		}

		public @NotNull List<Attribute> attribute() {
			return attribute;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj == null || obj.getClass() != this.getClass())
				return false;
			var that = (Basic) obj;
			return Objects.equals(this.material, that.material) && Objects.equals(this.name, that.name)
					&& Objects.equals(this.id, that.id) && Objects.equals(this.customProperties, that.customProperties)
					&& Objects.equals(this.attribute, that.attribute);
		}

		@Override
		public int hashCode() {
			return Objects.hash(material, name, id, customProperties, attribute);
		}

		@Override
		public String toString() {
			return "Basic[" + "material=" + material + ", " + "name=" + name + ", " + "id=" + id + ", "
					+ "customProperties=" + customProperties + ", " + "attribute=" + attribute + ']';
		}

		@Override
		@NotNull
		public List<BlockInteractListener> getInteractListeners() {
			return interactListeners;
		}

		@Override
		@NotNull
		public List<PlayerTickListener> getTickListeners() {
			return tickListeners;
		}
	}

}
