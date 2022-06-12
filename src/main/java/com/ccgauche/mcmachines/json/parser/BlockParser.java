package com.ccgauche.mcmachines.json.parser;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.json.FileException;
import com.ccgauche.mcmachines.json.JSONContext;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockParser {

	@NotNull
	public static Block parse(@NotNull JSONContext object) throws Exception {
		var k1 = StringParser.parse(object);
		if (!k1.contains(":"))
			throw new FileException(
					"Invalid JSON file \"" + object.jsonFile() + "\", expected identifier (" + object + ")");
		if (k1.startsWith("minecraft:")) {
			var j = Registry.BLOCK.get(new Identifier(k1));

			if (j == Blocks.AIR && !k1.equals("minecraft:air")) {
				throw new FileException("Invalid JSON file \"" + object.jsonFile() + "\", invalid identifier " + k1
						+ " (" + object + ")");
			}
			return j;
		}
		throw new FileException("Block identifier should start with minecraft:" + object.jsonFile());
	}
}
