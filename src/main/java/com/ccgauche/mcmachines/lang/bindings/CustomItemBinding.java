package com.ccgauche.mcmachines.lang.bindings;

import java.util.List;

import com.ccgauche.mcmachines.data.Attribute;
import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;
import com.ccgauche.mcmachines.registry.ItemRegistry;

@TypeAlias(name = "CustomItem")
public class CustomItemBinding {

	public static IItem.Basic $new(CItem material, String name, String id, DataCompound properties,
			List<Attribute> attributes) {
		return new IItem.Basic(material.getItem(), name, id, properties, attributes);
	}

	@Method
	public static IItem.Basic register(IItem.Basic item) {
		ItemRegistry.addItem(item);
		return item;
	}
}
