package rikka.librikka.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class BlockEntityHelper<T extends BlockEntity> {
	private final BlockEntityType<T> beType;
	private final BEConstructor<T> beConstructor;

	private BlockEntityHelper(BEConstructor<T> beConstructor, Block... validBlocks) {
		this.beType = BlockEntityType.Builder.of(this::create, validBlocks).build(null);
		this.beConstructor = beConstructor;
	}

	public T create(BlockPos pos, BlockState state) {
		return beConstructor.create(beType, pos, state);
	}

	@FunctionalInterface
	public static interface BEConstructor<T extends BlockEntity> {
		T create(BlockEntityType<?> beType, BlockPos pos, BlockState state);
	}

	/**
	 * @param beClass
	 * @return a generated registry name for a class (e.g. BlockEntity) based on its classpath
	 */
	public static String getRegistryName(Class<?> beClass) {
		return beClass.getName().toLowerCase().replace('$', '.');
	}

	/**
	 * Create a BlockEntityType with registry name set.
	 */
	public static <T extends BlockEntity> BlockEntityType<T> of(Class<T> beClass,
			BEConstructor<T> beConstructor, Block... validBlocks) {
		String registryName = getRegistryName(beClass);
		BlockEntityType<T> beType = (new BlockEntityHelper<T>(beConstructor, validBlocks)).beType;
		beType.setRegistryName(registryName);
		return beType;
	}

	/**
	 * Create a BlockEntityType with registry name set and register it.
	 */
	public static <T extends BlockEntity> BlockEntityType<T> register(
			IForgeRegistry<BlockEntityType<?>> registry, Class<T> beClass, BEConstructor<T> beConstructor,
			Block... validBlocks) {
		BlockEntityType<T> beType = BlockEntityHelper.of(beClass, beConstructor, validBlocks);

		if (BlockEntityHelper.registry == null) {
			BlockEntityHelper.registry = registry;
		}

		registry.register(beType);
		return beType;
	}

	/**
	 * @param <T>
	 * @param namespace e.g. {@code ModLoadingContext.get().getActiveNamespace()}
	 * @param beClass
	 * @return BlockEntityType from {@code beClass}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BlockEntity> BlockEntityType<T> getBEType(String namespace, Class<T> beClass) {
		String clsName = BlockEntityHelper.getRegistryName(beClass);
		ResourceLocation res = new ResourceLocation(namespace, clsName);

		if (registry == null) {
			registry = RegistryManager.ACTIVE.getRegistry(BlockEntityType.class);
		}

		return (BlockEntityType<T>) registry.getValue(res);
	}

	private static IForgeRegistry<BlockEntityType<?>> registry;
}
