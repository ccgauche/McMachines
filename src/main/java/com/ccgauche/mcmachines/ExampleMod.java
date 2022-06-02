package com.ccgauche.mcmachines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.*;
import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.machine.implementations.ConstantGeneratorTemplate;
import com.ccgauche.mcmachines.machine.implementations.SimpleChargerTemplate;
import com.ccgauche.mcmachines.machine.implementations.SimpleTransformerTemplate;
import com.ccgauche.mcmachines.registry.CraftRegistry;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.registry.ItemRegistry;
import com.ccgauche.mcmachines.registry.MachineRegistry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("modid");
	public static int KKK = 0;

	@Override
	public void onInitialize() {
		try {
			DataRegistry.load(new File("machines.dat"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		try {
			loadFiles();
		} catch (FileNotFoundException | FileException | NoSuchFieldException | InstantiationException
				| IllegalAccessException e) {
			System.out.println("Message: " + e.getMessage());
			throw new RuntimeException(e);
		}
		try {
			createTexturePack();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadFiles() throws FileNotFoundException, FileException, NoSuchFieldException,
			InstantiationException, IllegalAccessException {
		for (File f1 : Objects.requireNonNull(new File("pack/items").listFiles())) {
			for (File f2 : Objects.requireNonNull(f1.listFiles())) {
				String id = f1.getName() + ":" + f2.getName().split("\\.")[0];
				loadItemFile((ItemFile) DParser.parse(ItemFile.class.getTypeName(), DParser.readFile(f2)), id);
			}
		}
		for (File f1 : Objects.requireNonNull(new File("pack/machines").listFiles())) {
			for (File f2 : Objects.requireNonNull(f1.listFiles())) {
				String id = f1.getName() + ":" + f2.getName().split("\\.")[0];
				loadMachineFile(id, (Machine) DParser.parse(Machine.class.getTypeName(), DParser.readFile(f2)));
			}
		}
		for (File f1 : Objects.requireNonNull(new File("pack/crafts").listFiles())) {
			for (File f2 : Objects.requireNonNull(f1.listFiles())) {
				loadCraftFile(IRecipe.parse(DParser.readFile(f2)));
			}
		}
	}

	public static void loadItemFile(ItemFile item, String id) {
		ItemRegistry.addItem(new IItem.Basic(Objects.requireNonNull(item.base.getItem()), item.name, id,
				item.properties.orElse(null), item.handler.orElse(List.of())));
	}

	public static void loadCraftFile(IRecipe recipe) {
		Objects.requireNonNull(CraftRegistry.get(recipe.on())).bindCraftModel(recipe);
	}

	public static void loadMachineFile(String id, Machine machine) {
		if (machine.mode == MachineMode.CONSTANT_GENERATOR) {
			ConstantGeneratorTemplate impl = new ConstantGeneratorTemplate(id, machine.name,
					machine.properties.orElse(null), machine.conditions.orElse(null));
			MachineRegistry.add(impl);
			ItemRegistry.addItem(impl.getRegistryItem());
		} else if (machine.mode == MachineMode.SIMPLE_TRANSFORMER) {
			SimpleTransformerTemplate impl = new SimpleTransformerTemplate(id, machine.name,
					machine.properties.orElse(null), machine.conditions.orElse(null));
			MachineRegistry.add(impl);
			ItemRegistry.addItem(impl.getRegistryItem());
		} else if (machine.mode == MachineMode.SIMPLE_CHARGER) {
			SimpleChargerTemplate impl = new SimpleChargerTemplate(id, machine.name, machine.properties.orElse(null),
					machine.conditions.orElse(null));
			MachineRegistry.add(impl);
			ItemRegistry.addItem(impl.getRegistryItem());
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void createTexturePack() throws IOException {
		new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/").mkdirs();
		Files.writeString(new File("pack/GeneratedPack/pack.mcmeta").toPath(), """
				{
				  "pack": {
				    "pack_format": 7,
				    "description": "Auto generated MCMachines texture pack"
				  }
				}""");
		for (Map.Entry<String, IItem> item : ItemRegistry.getItems().entrySet()) {
			String baseName = item.getKey().split(":")[0];
			String nextPath = item.getKey().substring(baseName.length() + 1);
			File file = new File("pack/textures/" + baseName + "/" + nextPath + ".png");
			if (file.exists()) {
				String mod_name = baseName + "/" + nextPath;
				Identifier id = Registry.ITEM.getId(item.getValue().material());
				new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/" + baseName).mkdirs();
				FileUtils.copyFile(file,
						new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/" + mod_name + ".png"));
				Files.writeString(
						new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/" + mod_name + ".properties")
								.toPath(),
						"texture=./" + nextPath + ".png\n" + "type=item\n" + "items=" + id.getPath() + "\n"
								+ "nbt.display.Lore.*=pattern:" + baseName + ":" + nextPath);
			}
		}
	}

}
