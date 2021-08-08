package rikka.librikka;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Utils {
    private final static Direction[] horizontalDirections = new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
    // SWNE
    // 0123

    /**
     * @param player
     * @return the direction where the player/entity is looking at
     */
    public static final Direction getPlayerSight(Entity player) {
        int pitch = Math.round(player.getXRot());

        if (pitch >= 65)
            return Direction.DOWN;  //1

        if (pitch <= -65)
            return Direction.UP;    //0

        return getPlayerSightHorizontal(player);
    }

    public static final Direction getPlayerSightHorizontal(Entity player) {
        int heading = Mth.floor(player.getYRot() * 4.0F / 360.0F + 0.5D) & 3;

        return horizontalDirections[heading];
    }

    /**
     * Drop an itemstack as entity
     */
    public static final void dropItemIntoWorld(Level world, Vec3i pos, ItemStack item) {
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
            entityItem.setDeltaMovement(
            		rand.nextGaussian() * factor,
            		rand.nextGaussian() * factor + 0.2F,
            		rand.nextGaussian() * factor);
            world.addFreshEntity(entityItem);
            item.setCount(0);
        }
    }

    public static void chat(Player player, String text) {
        player.sendMessage(new TextComponent(text), net.minecraft.Util.NIL_UUID);
    }

    public static void chatWithLocalization(Player player, String text) {
        player.sendMessage(new TranslatableComponent(text), net.minecraft.Util.NIL_UUID);
    }

    public static void saveToNbt(CompoundTag nbt, String name, Direction facing) {
        if (facing == null)
            return;

        nbt.putByte(name, (byte) facing.ordinal());
    }

    public static Direction facingFromNbt(CompoundTag nbt, String name) {
        if (!nbt.contains(name))
            return null;

        return Direction.from3DDataValue(nbt.getByte(name));
    }

    public static void saveToNbt(CompoundTag nbt, String prefix, BlockPos pos) {
        if (pos == null)
            return;

        nbt.putInt(prefix + "X", pos.getX());
        nbt.putInt(prefix + "Y", pos.getY());
        nbt.putInt(prefix + "Z", pos.getZ());
    }

    public static BlockPos posFromNbt(CompoundTag nbt, String prefix) {
        if (!nbt.contains(prefix + "Y"))
            return null;

        int x = nbt.getInt(prefix + "X");
        int y = nbt.getInt(prefix + "Y");
        int z = nbt.getInt(prefix + "Z");

        if (y < 0)
            return null;

        return new BlockPos(x, y, z);
    }

    public static CompletableFuture<Void> enqueueServerWork(Runnable runnable) {
    	MinecraftServer executor = LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER);
        // Must check ourselves as Minecraft will sometimes delay tasks even when they are received on the client thread
        // Same logic as ThreadTaskExecutor#runImmediately without the join
        if (!executor.isSameThread()) {
            return executor.submitAsync(runnable); // Use the internal method so thread check isn't done twice
        } else {
            runnable.run();
            return CompletableFuture.completedFuture(null);
        }
    }
}
