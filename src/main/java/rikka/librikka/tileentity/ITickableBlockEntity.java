package rikka.librikka.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/***
 * A wrapper interface that forwards server tick event to TileEntity instance.
 */
public interface ITickableBlockEntity {
	void tick();

	public static <T extends BlockEntity> void genericTicker(Level level, BlockPos pos, BlockState blockState, T te) {
		if (level.isClientSide())
			return;

		if (te instanceof ITickableBlockEntity)
			((ITickableBlockEntity) te).tick();
	}
}
