package rikka.librikka;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;

public interface ITileMeta extends IMetaBase {
	Class<? extends BlockEntity> teCls();

	default BlockEntitySupplier<?> getBlockEntitySupplier() {
		Class<? extends BlockEntity> teCls = teCls();
		if (teCls == null)
			return null;

		try {
			Constructor<? extends BlockEntity> constructor = teCls().getConstructor(BlockPos.class, BlockState.class);
			return (pos, state) -> {
				try {
					return constructor.newInstance(pos, state);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return null;
			};
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	default BlockEntity create(BlockPos pos, BlockState state) {
		return getBlockEntitySupplier().create(pos, state);
	}
}
