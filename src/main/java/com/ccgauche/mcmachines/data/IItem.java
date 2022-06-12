package com.ccgauche.mcmachines.data;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.utils.TextUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public interface IItem {

	@NotNull
	ItemStack create(int amount, @Nullable DataCompound map);

	@NotNull
	String id();

	@NotNull
	Item material();

	@NotNull
	List<String> handlers();

	record Basic(@NotNull Item material, @NotNull String name, @NotNull String id,
			@Nullable DataCompound customProperties, @NotNull List<String> handlers, @NotNull List<Attribute> attribute)
			implements IItem {

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

	}

}
