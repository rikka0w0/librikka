package rikka.librikka.multiblock;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import rikka.librikka.Utils;

/**
 * Additional multiblock information that stored in tileEntities 
 * @author Rikka0w0
 */
public class MultiBlockTileInfo {
    public final Direction facing;
    public final boolean mirrored;
	/**the coordinate in the structure description (before rotation and mirror)*/
    public final int xOffset, yOffset, zOffset;
    public final BlockPos origin;
    protected boolean formed;

    /**
     * Structure creation
     *
     * @param facing
     * @param mirrored
     * @param xOffset
     * @param yOffset
     * @param zOffset
     * @param xOrigin
     * @param yOrigin
     * @param zOrigin
     */
    public MultiBlockTileInfo(Direction facing, boolean mirrored, int xOffset, int yOffset, int zOffset, int xOrigin, int yOrigin, int zOrigin) {
        this.facing = facing;
        this.mirrored = mirrored;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.origin = new BlockPos(xOrigin, yOrigin, zOrigin);
        this.formed = true;
    }

    public MultiBlockTileInfo(CompoundNBT nbt) {
    	this.facing = Utils.facingFromNbt(nbt, "facing");
    	this.mirrored = nbt.getBoolean("mirrored");
    	this.xOffset = nbt.getInt("xOffset");
    	this.yOffset = nbt.getInt("yOffset");
    	this.zOffset = nbt.getInt("zOffset");
        this.origin = Utils.posFromNbt(nbt, "origin");
        this.formed = nbt.getBoolean("formed");
    }

    public void saveToNBT(CompoundNBT nbt) {
        Utils.saveToNbt(nbt, "facing", this.facing);
        nbt.putBoolean("mirrored", this.mirrored);
        nbt.putInt("xOffset", this.xOffset);
        nbt.putInt("yOffset", this.yOffset);
        nbt.putInt("zOffset", this.zOffset);
        Utils.saveToNbt(nbt, "origin", this.origin);
        nbt.putBoolean("formed", this.formed);
    }

    public boolean isPart(Vector3i partPos) {
    	return xOffset == partPos.getX() && yOffset == partPos.getY() && zOffset == partPos.getZ();
    }
    
    /**
     * @param offsetPos the coordinate in the structure description (before rotation and mirror)
     * @return the actual BlockPos
     */
    public BlockPos getPartPos(Vector3i offsetPos) {
        int[] offset = MultiBlockStructure.offsetFromOrigin(facing.ordinal()-2, this.mirrored,
                offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
        return this.origin.add(offset[0], offset[1], offset[2]);
    }
    
    /**
     * @param array y,z,x facing NORTH(Z-)
     * @return
     */
    public <T> T lookup(T[][][] array) {
    	if (this.yOffset >= array.length)
    		return null;
    	
    	T[][] renderInfoZX = array[this.yOffset];
    	if (this.zOffset >= renderInfoZX.length)
    		return null;
    	
    	T[] renderInfoX = renderInfoZX[this.zOffset];
    	if (this.xOffset >= renderInfoX.length)
    		return null;
    	
    	return renderInfoX[this.xOffset];
    }
    
    public static <T> T lookup(IMultiBlockTile mbTile, T[][][] array) {
    	if (mbTile == null)
    		return null;
    	
    	MultiBlockTileInfo mbInfo = mbTile.getMultiBlockTileInfo();
    	if (mbInfo == null)
    		return null;
    	
    	return mbInfo.lookup(array);
    }
}
