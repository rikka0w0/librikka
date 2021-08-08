package rikka.librikka.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;

public class BlockUtils {
    public static boolean isSideSolid(BlockGetter world, BlockPos pos, Direction side) {
    	BlockState blockstate = world.getBlockState(pos);
    	return blockstate.isFaceSturdy(world, pos, side);
    }

    /**
     * Retrieve a BlockEntity safely, if not present, return null
     * <br>
     * https://mcforge.readthedocs.io/en/latest/blockstates/states/#actual-states
     * <br> In 1.11.2 Forge has created a patch for this problem, see {@link ChunkCache#getTileEntity(BlockPos)}
     *
     * @param world
     * @param pos
     * @return
     */
    @SuppressWarnings("deprecation")
	public static BlockEntity getTileEntitySafely(LevelReader world, BlockPos pos) {
    	// TODO: fix getTileEntitySafely() ChunkPrimer
    	return world.hasChunkAt(pos) ? world.getBlockEntity(pos) : null;
//        return world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.CreateEntityType.CHECK) : world.getTileEntity(pos);
    }

    public static Component getDisplayName(Level world, BlockPos pos) {
    	BlockEntity te = world.getBlockEntity(pos);
    	if (te instanceof MenuProvider)
    		return ((MenuProvider) te).getDisplayName();

    	BlockState blockstate = world.getBlockState(pos);
    	MenuProvider container = blockstate.getMenuProvider(world, pos);
    	if (container != null)
    		return container.getDisplayName();

    	return new TranslatableComponent(blockstate.getBlock().getDescriptionId());
    }
}
