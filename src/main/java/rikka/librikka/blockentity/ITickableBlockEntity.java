package rikka.librikka.blockentity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/***
 * A wrapper interface that forwards server tick event to TileEntity instance.
 */
public interface ITickableBlockEntity {
	void tick();

	/**
	 * Called in {@link net.minecraft.world.level.block.EntityBlock#getTicker(Level, BlockState, net.minecraft.world.level.block.entity.BlockEntityType)}
	 * <p>
	 * Example:
	 * <pre>
	 * {@code
	 * public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> beType)
	 * 	return meta.tickable && !level.isClientSide() ? ITickableBlockEntity::serverTicker : null;
	 * }
	 * </pre>
	 */
	public static <T extends BlockEntity> void serverTicker(Level level, BlockPos pos, BlockState blockState, T te) {
		if (level.isClientSide())
			return;

		if (te instanceof ITickableBlockEntity)
			((ITickableBlockEntity) te).tick();
	}
}
