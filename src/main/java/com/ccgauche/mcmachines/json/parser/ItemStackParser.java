package com.ccgauche.mcmachines.json.parser;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.JSONContext;

import net.minecraft.item.ItemStack;

public class ItemStackParser {

	@NotNull
	public static ItemStack parse(@NotNull JSONContext object) throws FileException {
		if (object.object() == null) {
			throw new FileException("Invalid ItemStack found null " + object.jsonFile());
		}
		if (object.object().isJsonObject()) {
			var data = object.object().getAsJsonObject();

			var k1 = data.get("type");
			if (k1 == null)
				throw new FileException(
						"Invalid JSON file \"" + object.jsonFile() + "\", expected type (" + object + ")");
			var k2 = ItemParser.parse(object.swap(k1));
			var k3 = data.get("properties");
			DataCompound compound;
			if (k3 == null) {
				compound = new DataCompound();
			} else {
				compound = DataCompoundParser.parse(object.swap(k3));

			}
			var k5 = data.get("amount");
			int amount;
			if (k5 == null) {
				amount = 1;
			} else {
				amount = IntParser.parse(object.swap(k5));
			}

			return k2.asStack(amount, compound);
		} else {
			return ItemParser.parse(object).asStack(1);
		}
	}
}
