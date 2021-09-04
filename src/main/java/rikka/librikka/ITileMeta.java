package rikka.librikka;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface ITileMeta extends IMetaBase {
	Class<? extends BlockEntity> teCls();

	BlockEntityType<?> beType();

	default BlockEntity create(BlockPos pos, BlockState state) {
		BlockEntityType<?> beType = beType();
		return beType == null ? null : beType.create(pos, state);
	}
}
