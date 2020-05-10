package rikka.librikka.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class BlockUtils {
    public static boolean isSideSolid(IBlockReader world, BlockPos pos, Direction side) {
    	BlockState blockstate = world.getBlockState(pos);
    	return blockstate.isSolidSide(world, pos, side);
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
}
