package com.ccgauche.mcmachines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.IItem;
import com.ccgauche.mcmachines.lang.Context;
import com.ccgauche.mcmachines.lang.Engine;
import com.ccgauche.mcmachines.registry.DataRegistry;
import com.ccgauche.mcmachines.registry.ItemRegistry;

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
		// Engine.main();
		try {
			DataRegistry.load(new File("machines.dat"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			loadFiles();
		} catch (FileNotFoundException | NoSuchFieldException | InstantiationException
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

	/*
	 * This method is used to load all the files in the "machines" folder (Only for
	 * textures).
	 */
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

	/*
	 * This method tries to load all the executable files in the "machines" folder
	 * (ending with .rs).
	 */
	public static void getExecutables(File folder, ExceptionConsumer consumer) {
		if (!folder.exists())
			folder.mkdirs();
		for (File f1 : Objects.requireNonNull(folder.listFiles())) {
			if (f1.isDirectory()) {

				getExecutables(f1, consumer);

			} else if (f1.getName().endsWith(".rs")) {
				try {
					consumer.run("", f1);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		}
	}

	/*
	 * Interface used to throw exceptions in lambdas. Only used in this class.
	 */
	interface ExceptionConsumer {

		void run(@NotNull String modName, @NotNull File file) throws Exception;
	}

	/*
	 * Loads the textures and the executable files.
	 */
	public static void loadFiles()
			throws FileNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException {
		Context ctx = new Context(new HashMap<>(2048));
		Engine.loadDefaults(ctx);
		getExecutables(new File("pack/mods"), (modName, file) -> {
			ctx.execute(file);
		});
	}

	/*
	 * Creates a texture pack with all the textures in the "pack" folder.
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void createTexturePack() throws IOException {
		new File("pack/GeneratedPack/").delete();
		// We are using McPatcher to apply the custom textures. So we need to place
		// everything in the "mcpatcher/cit" folder.
		new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/").mkdirs();
		/*
		 * Creates the manifest file for the texture pack.
		 */
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
			/*
			 * Used by armors to know which layer to apply the texture to.
			 */
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
			// Writes the texture to the texture pack.
			FileUtils.copyFile(file,
					new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/" + mod + "/" + file_name + ".png"));
			// Writes the binding to the item.
			Files.writeString(
					new File("pack/GeneratedPack/assets/minecraft/mcpatcher/cit/" + mod + "/" + file_name
							+ ".properties").toPath(),
					"texture" + (layer == 0 ? "" : ("." + id.getPath().split("_")[0] + "_layer_" + layer)) + "=./"
							+ file_name + ".png\n" + "type=" + (layer == 0 ? "item" : "armor") + "\n" + "items="
							+ id.getPath() + "\n" + "nbt.display.Lore.*=pattern:" + mod + ":" + item_name);
		});
	}

	/*
	 * Gets the armor player model name for a given item.
	 */
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
