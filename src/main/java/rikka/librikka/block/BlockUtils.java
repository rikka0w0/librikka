package rikka.librikka.block;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

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
    @SuppressWarnings("deprecation")
	public static TileEntity getTileEntitySafely(IWorldReader world, BlockPos pos) {
    	// TODO: fix getTileEntitySafely() ChunkPrimer
    	return world.isBlockLoaded(pos) ? world.getTileEntity(pos) : null;
//        return world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.CreateEntityType.CHECK) : world.getTileEntity(pos);
    }
    
    public static ITextComponent getDisplayName(World world, BlockPos pos) {
    	TileEntity te = world.getTileEntity(pos);
    	if (te instanceof INamedContainerProvider)
    		return ((INamedContainerProvider) te).getDisplayName();

    	BlockState blockstate = world.getBlockState(pos);
    	INamedContainerProvider container = blockstate.getContainer(world, pos);
    	if (container != null)
    		return container.getDisplayName();

    	return new TranslationTextComponent(blockstate.getBlock().getTranslationKey());
    }
}
