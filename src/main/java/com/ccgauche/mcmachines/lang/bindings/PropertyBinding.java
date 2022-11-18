package com.ccgauche.mcmachines.lang.bindings;

import java.lang.reflect.Field;
import java.util.Comparator;

import com.ccgauche.mcmachines.lang.TypeAlias;

import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;

@TypeAlias(name = "Property")
public class PropertyBinding {

	public static Property<?> of(String name) {

		Integer in = null;
		try {
			if (name.contains("_")) {
				String[] split = name.split("_");
				in = Integer.parseInt(split[split.length - 1]);
				name = name.substring(0, name.length() - split[split.length - 1].length() - 1);
			}
		} catch (NumberFormatException ignored) {
		}

		for (Field property : Properties.class.getDeclaredFields()) {
			if (!Property.class.isAssignableFrom(property.getType())) {
				continue;
			}
			try {
				if (((Property<?>) property.get(null)).getName().equalsIgnoreCase(name)) {

					if (in != null) {
						if (property.getType() != IntProperty.class) {
							continue;
						}
						IntProperty intProperty = (IntProperty) property.get(null);
						if (intProperty.getValues().stream().max(Comparator.naturalOrder()).get().equals(in)) {
							return intProperty;
						} else {
							continue;
						}
					}
					return (Property<?>) property.get(null);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
}
