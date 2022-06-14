package com.ccgauche.mcmachines.utils;

import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.json.Dual;

import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ItemUtils {

	public static BlockPos getBlockFacing(Direction dir, BlockPos pos) {
		return new BlockPos(dir.getOffsetX() + pos.getX(), dir.getOffsetY() + pos.getY(),
				dir.getOffsetZ() + pos.getZ());
	}

	private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

	public static void dispense(ServerWorld world, BlockPos pos, ItemStack itemStack) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
		ItemStack itemStack2;
		if (itemStack.isEmpty())
			return;
		Direction direction = world.getBlockState(pos).get(Properties.FACING);
		Inventory inventory = HopperBlockEntity.getInventoryAt(world, pos.offset(direction));
		if (inventory == null) {
			itemStack2 = itemStack;
		} else {
			itemStack2 = HopperBlockEntity.transfer(null, inventory, itemStack.copy(), direction.getOpposite());
		}
		if (itemStack2 != null && !itemStack2.isEmpty()) {
			BEHAVIOR.dispense(blockPointerImpl, itemStack);
		}
	}

	public static boolean containsEnoughItem(CItem cItem, int number, Inventory inv) {
		for (int i = 0; i < 9; i++) {
			ItemStack item = inv.getStack(i);
			if (item == null || item.getItem() == null || item.isEmpty())
				continue;
			CItem cItem1 = new CItem(item);
			if (cItem1.equals(cItem)) {
				number -= item.getCount();
				if (number < 1) {
					return true;
				}
			}
		}
		return false;
	}

	public static void removeItem(CItem cItem, int number, Inventory inv) {
		for (int i = 0; i < 9; i++) {
			ItemStack item = inv.getStack(i);
			if (item == null || item.getItem() == null || item.isEmpty())
				continue;
			CItem cItem1 = new CItem(item);
			if (cItem1.equals(cItem)) {
				int toRemove = Math.min(item.getCount(), number);
				number -= toRemove;
				ItemUtils.removeItem(inv, i, toRemove);
				if (number == 0) {
					return;
				}
			}
		}
	}

	public static void outputItems(ServerWorld world, BlockPos pos, List<Dual<ItemStack, Integer>> outputs) {
		for (var toDrop : outputs) {
			ItemStack stack = toDrop.first().copy();
			int multiplier = toDrop.second() / 100 + ((new Random().nextInt(100) <= toDrop.second() % 100) ? 1 : 0);
			stack.setCount(stack.getCount() * multiplier);
			if (stack.getCount() == 0) {
				continue;
			}
			ItemUtils.dispense(world, pos, stack);
		}
	}

	public static boolean removeItem(Inventory blockEntity, int slot, int n) {
		int count = blockEntity.getStack(slot).getCount();
		if (count < n) {
			return false;
		}
		if (count == n)
			blockEntity.removeStack(slot);
		else
			blockEntity.removeStack(slot, n);
		return true;
	}

	public static boolean setLore(@NotNull ItemStack i, @Nullable List<Text> loreText) {
		NbtCompound display = i.getOrCreateSubNbt("display");
		if (display != null) {
			if (display.contains("Lore")) {
				NbtList lore = display.getList("Lore", NbtElement.STRING_TYPE);
				lore.clear();
				if (loreText != null && !loreText.isEmpty()) {
					for (Text t : loreText) {
						lore.add(NbtString.of(Text.Serializer.toJson(t)));
					}
				}
				return true;
			} else {
				NbtList lore = new NbtList();
				if (loreText != null && !loreText.isEmpty()) {
					for (Text t : loreText) {
						lore.add(NbtString.of(Text.Serializer.toJson(t)));
					}
				}
				display.put("Lore", lore);
			}
		}
		return false;
	}
}
