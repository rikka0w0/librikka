package rikka.librikka.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickableTileEntity {
	void tick();

	default boolean tickOnServer() {
		return true;
	}

	default boolean tickOnClient() {
		return false;
	}

	public static <T extends BlockEntity> void genericTicker(Level level, BlockPos pos, BlockState blockState, T te) {
		if (te instanceof ITickableTileEntity) {
			ITickableTileEntity tickable = (ITickableTileEntity) te;

			if (
					(!level.isClientSide() && tickable.tickOnServer()) ||
					(level.isClientSide() && tickable.tickOnClient())
				)
				((ITickableTileEntity) te).tick();
		}
	}
}
