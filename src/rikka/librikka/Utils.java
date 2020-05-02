package rikka.librikka;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Utils {
//    public static final Direction[] horizontalInverted = new Direction[]{Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST};
    private final static Direction[] horizontalDirections = new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
    // SWNE
    // 0123
    /**
     * @param player
     * @return the direction where the player/entity is looking at
     */
    public static final Direction getPlayerSight(Entity player) {
        int pitch = Math.round(player.rotationPitch);

        if (pitch >= 65)
            return Direction.DOWN;  //1

        if (pitch <= -65)
            return Direction.UP;    //0

        return getPlayerSightHorizontal(player);
    }

    public static final Direction getPlayerSightHorizontal(Entity player) {
        int heading = MathHelper.floor(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

        return horizontalDirections[heading];
    }
    
    /**
     * Drop items inside the inventory
     */
    public static final void dropItemIntoWorld(World world, BlockPos pos, ItemStack item) {
        Random rand = new Random();

        if (item != null && item.getCount() > 0) {
            float rx = rand.nextFloat() * 0.8F + 0.1F;
            float ry = rand.nextFloat() * 0.8F + 0.1F;
            float rz = rand.nextFloat() * 0.8F + 0.1F;

            ItemEntity entityItem = new ItemEntity(world,
                    pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
                    item.copy());

            if (item.hasTag()) {
                entityItem.getItem().setTag(item.getTag().copy());
            }

            float factor = 0.05F;
            entityItem.setMotion(
            		rand.nextGaussian() * factor, 
            		rand.nextGaussian() * factor + 0.2F,
            		rand.nextGaussian() * factor);
            world.addEntity(entityItem);
            item.setCount(0);
        }
    }

    public static void chat(PlayerEntity player, String text) {
        player.sendMessage(new StringTextComponent(text));
    }

    public static void chatWithLocalization(PlayerEntity player, String text) {
        player.sendMessage(new TranslationTextComponent(text));
    }

    public static void saveToNbt(CompoundNBT nbt, String name, Direction facing) {
        if (facing == null)
            return;

        nbt.putByte(name, (byte) facing.ordinal());
    }

    public static Direction facingFromNbt(CompoundNBT nbt, String name) {
        if (!nbt.contains(name))
            return null;

        return Direction.byIndex(nbt.getByte(name));
    }

    public static void saveToNbt(CompoundNBT nbt, String prefix, BlockPos pos) {
        if (pos == null)
            return;

        nbt.putInt(prefix + "X", pos.getX());
        nbt.putInt(prefix + "Y", pos.getY());
        nbt.putInt(prefix + "Z", pos.getZ());
    }

    public static BlockPos posFromNbt(CompoundNBT nbt, String prefix) {
        if (!nbt.contains(prefix + "Y"))
            return null;

        int x = nbt.getInt(prefix + "X");
        int y = nbt.getInt(prefix + "Y");
        int z = nbt.getInt(prefix + "Z");

        if (y < 0)
            return null;

        return new BlockPos(x, y, z);
    }

    /**
     * Retrieve a TileEntity safely, if not present, return null
     * <br>
     * https://mcforge.readthedocs.io/en/latest/blockstates/states/#actual-states
     * <br> In 1.11.2 Forge has created a patch for this problem, see {@link ChunkCache#getTileEntity(BlockPos)}
     *
     * @param world
     * @param pos
     * @return
     */
    @Deprecated
    public static TileEntity getTileEntitySafely(IWorldReader world, BlockPos pos) {
    	// TODO: fix getTileEntitySafely() ChunkPrimer
    	return world.isBlockLoaded(pos) ? world.getTileEntity(pos) : null;
//        return world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.CreateEntityType.CHECK) : world.getTileEntity(pos);
    }
    
    public static CompletableFuture<Void> enqueueServerWork(Runnable runnable) {
        ThreadTaskExecutor<?> executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
        // Same logic as ThreadTaskExecutor#runImmediately without the join
        if (!executor.isOnExecutionThread()) {
            return executor.deferTask(runnable); // Use the internal method so thread check isn't done twice
        } else {
            runnable.run();
            return CompletableFuture.completedFuture(null);
        }
    }
    
    public static boolean isSideSolid(IBlockReader world, BlockPos pos, Direction side) {
    	BlockState blockstate = world.getBlockState(pos);
    	return blockstate.isSolidSide(world, pos, side);
    }
}
