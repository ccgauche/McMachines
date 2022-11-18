package com.ccgauche.mcmachines.lang.bindings;

import com.ccgauche.mcmachines.lang.Method;
import com.ccgauche.mcmachines.lang.TypeAlias;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

@TypeAlias(name = "BlockPos")
public class BlockPosBinding {

	@Method
	public static int y(BlockPos pos) {
		return pos.getY();
	}

	@Method
	public static int x(BlockPos pos) {
		return pos.getX();
	}

	@Method
	public static int z(BlockPos pos) {
		return pos.getZ();
	}

	@Method
	public static Direction direction(World world, BlockPos pos) {
		return world.getBlockState(pos).get(Properties.FACING);
	}

	@Method
	public static int getSkyLight(World world, BlockPos pos) {
		return world.getLightLevel(LightType.SKY, pos);
	}

	@Method
	public static Block blockType(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock();
	}

	@Method
	public static Item blockMaterial(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().asItem();
	}

	@Method
	public static BlockState blockName(World world, BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Method
	public static boolean isPowered(World world, BlockPos pos) {
		return world.isReceivingRedstonePower(pos);
	}

	@Method
	public static BlockPos facing(World world, BlockPos pos) {
		return pos.offset(direction(world, pos));
	}

	@Method
	public static Object property(World world, Property<?> prop, BlockPos pos) {
		return world.getBlockState(pos).getEntries().get(prop);
	}

	@Method
	public static boolean isPoweredFrom(World world, Direction direction, BlockPos pos) {
		return world.isReceivingRedstonePower(pos.offset(direction));
	}

	@Method
	public static void $break(World world, BlockPos pos) {
		world.breakBlock(pos, true);
	}

	@Method
	public static BlockPos setBlock(World world, Block block, BlockPos pos) {
		world.setBlockState(pos, block.getDefaultState());
		return pos;
	}

}
