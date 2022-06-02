package com.ccgauche.mcmachines.utils;

import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemUtils {

    public static BlockPos getBlockFacing(Direction dir, BlockPos pos) {
        return new BlockPos(dir.getOffsetX() + pos.getX(), dir.getOffsetY() + pos.getY(), dir.getOffsetZ() + pos.getZ());
    }

    @Nullable
    public static LootableContainerBlockEntity getInventory(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) return null;
        if (!(blockEntity instanceof LootableContainerBlockEntity)) return null;
        return (LootableContainerBlockEntity) blockEntity;
    }

    private static final DispenserBehavior BEHAVIOR = (DispenserBehavior)new ItemDispenserBehavior();


    public static void dispense(ServerWorld world, BlockPos pos, ItemStack itemStack) {
        BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
        ItemStack itemStack2;
        if (itemStack.isEmpty())
            return;
        Direction direction = world.getBlockState(pos).get(Properties.FACING);
        Inventory inventory = HopperBlockEntity.getInventoryAt((World)world, pos.offset(direction));
        if (inventory == null) {
            itemStack2 = itemStack;
        } else {
            itemStack2 = HopperBlockEntity.transfer(null, inventory, itemStack.copy(), direction.getOpposite());
        }
        if (itemStack2 != null && !itemStack2.isEmpty()) {
            BEHAVIOR.dispense(blockPointerImpl, itemStack);
        }
    }

    public static boolean removeItem(LootableContainerBlockEntity blockEntity, int slot, int n) {
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
