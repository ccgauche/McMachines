package com.ccgauche.mcmachines.json.parser;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.JSONContext;

import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemParser {

	@NotNull
	public static CItem parse(@NotNull JSONContext object) throws FileException {
		var k2 = StringParser.parse(object);
		if (!k2.contains(":"))
			throw new FileException(
					"Invalid JSON file \"" + object.jsonFile() + "\", expected identifier (" + object + ")");
		if (k2.startsWith("minecraft:")) {
			var j = Registry.ITEM.get(new Identifier(k2));
			if (j == Items.AIR && !k2.equals("minecraft:air")) {
				throw new FileException("Invalid JSON file \"" + object.jsonFile() + "\", invalid identifier " + k2
						+ " (" + object + ")");
			}
			return new CItem(j);
		}
		return new CItem(k2);
	}
}
