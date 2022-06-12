package com.ccgauche.mcmachines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.json.*;
import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.machine.implementations.BurningGeneratorTemplate;
import com.ccgauche.mcmachines.machine.implementations.ConstantGeneratorTemplate;
import com.ccgauche.mcmachines.machine.implementations.SimpleChargerTemplate;
import com.ccgauche.mcmachines.machine.implementations.SimpleTransformerTemplate;
import com.ccgauche.mcmachines.registry.CraftRegistry;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.registry.ItemRegistry;
import com.ccgauche.mcmachines.registry.MachineRegistry;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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

	public static void crawlUntilFolder(String name, File folder, @Nullable String modname,
			ExceptionConsumer consumer) {
		if (!folder.exists())
			folder.mkdirs();
		for (File f1 : Objects.requireNonNull(folder.listFiles())) {
			if (f1.isDirectory()) {
				if (f1.getName().equalsIgnoreCase(name)) {
					for (File f2 : Objects.requireNonNull(f1.listFiles())) {
						if (modname != null) {
							try {
								consumer.run(modname, f2);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					}
				} else {
					crawlUntilFolder(name, f1,
							!f1.getName().startsWith("@") && modname == null ? f1.getName() : modname, consumer);
				}
			}
		}
	}

	interface ExceptionConsumer {

		void run(@NotNull String modName, @NotNull File file) throws Exception;
	}

	public static void loadFiles() throws FileNotFoundException, FileException, NoSuchFieldException,
			InstantiationException, IllegalAccessException {
		crawlUntilFolder("items", new File("pack/mods"), null, (mod, file) -> {
			String id = mod + ":" + file.getName().split("\\.")[0];
			loadItemFile((ItemFile) DParser.parse(ItemFile.class.getTypeName(), DParser.readFile(file)), id);
			System.out.println("Item " + id + " loaded");
		});
		crawlUntilFolder("machines", new File("pack/mods"), null, (mod, file) -> {
			String id = mod + ":" + file.getName().split("\\.")[0];
			loadMachineFile(id, (Machine) DParser.parse(Machine.class.getTypeName(), DParser.readFile(file)));
			System.out.println("Machine " + id + " loaded");
		});
		crawlUntilFolder("crafts", new File("pack/mods"), null, (mod, file) -> {
			loadCraftFile(IRecipe.parse(DParser.readFile(file)));
			System.out.println("Craft loaded: " + file);
		});
	}

	public static void loadItemFile(ItemFile item, String id) {
		ItemRegistry.addItem(new IItem.Basic(Objects.requireNonNull(item.base.getItem()), item.name, id,
				item.properties.orElse(null), item.handler.orElse(List.of()), item.attributes.orElse(List.of())));
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
		} else if (machine.mode == MachineMode.BURNING_GENERATOR) {
			BurningGeneratorTemplate impl = new BurningGeneratorTemplate(id, machine.name,
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
		new File("pack/GeneratedPack/").delete();
		new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/").mkdirs();
		Files.writeString(new File("pack/GeneratedPack/pack.mcmeta").toPath(), """
				{
				  "pack": {
				    "pack_format": 7,
				    "description": "Auto generated MCMachines texture pack"
				  }
				}""");
		crawlUntilFolder("textures", new File("pack/mods"), null, (mod, file) -> {
			String file_name = file.getName().split("\\.")[0];
			String item_name = file_name;
			int layer = 0;
			if (file_name.endsWith("_layer_1") || file_name.endsWith("_layer_2")) {
				layer = Integer.parseInt(file_name.split("_layer_")[1]);
				item_name = file_name.split("_layer_")[0];
			}
			IItem item = ItemRegistry.getItem(mod + ":" + item_name);
			if (item == null) {
				System.out.println("WARNING: Unused texture " + file_name + ".png in " + mod);
				return;
			}
			Identifier id = Registry.ITEM.getId(item.material());
			FileUtils.copyFile(file,
					new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/" + mod + "/" + file_name + ".png"));
			Files.writeString(
					new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/" + mod + "/" + file_name
							+ ".properties").toPath(),
					"texture" + (layer == 0 ? "" : ("." + id.getPath().split("_")[0] + "_layer_" + layer)) + "=./"
							+ file_name + ".png\n" + "type=" + (layer == 0 ? "item" : "armor") + "\n" + "items="
							+ id.getPath() + "\n" + "nbt.display.Lore.*=pattern:" + mod + ":" + item_name);
		});
	}

	public static int getArmorLayer(Item item) {
		Identifier id = Registry.ITEM.getId(item);
		if (id.getPath().endsWith("_chestplate") || id.getPath().endsWith("_helmet") || id.getPath().endsWith("_boots")
				|| item == Items.CARVED_PUMPKIN) {
			return 1;
		}
		if (id.getPath().endsWith("_leggings")) {
			return 2;
		}
		return 0;
	}

}
