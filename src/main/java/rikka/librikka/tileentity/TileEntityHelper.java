package rikka.librikka.tileentity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class TileEntityHelper {
	private static IForgeRegistry<BlockEntityType<?>> registry;
	public static String getRegistryName(Class<?> teClass) {
		String registryName = teClass.getName().toLowerCase().replace('$', '.');

		return registryName;
	}

	@SuppressWarnings("unchecked")
	public static <T extends BlockEntity> BlockEntityType<T> getTeType(String namespace, Class<T> teClass) {
		String clsName = TileEntityHelper.getRegistryName(teClass);
		ResourceLocation res = new ResourceLocation(namespace, clsName);

		if (registry == null) {
			registry = RegistryManager.ACTIVE.getRegistry(BlockEntityType.class);
		}

		return (BlockEntityType<T>) registry.getValue(res);
	}


    /**
     * Create a BlockEntityType and register it with a default registry name
     */
    public static <T extends BlockEntity> BlockEntityType<T> createTeType(Class<T> teClass, Block... validBlocks) {
    	String registryName = getRegistryName(teClass);

    	TileEntityConstructorSupplier<T> constructorSupplier;
		try {
			constructorSupplier = new TileEntityConstructorSupplier<T>(teClass);
		} catch (RuntimeException e) {
			return null;
		}

    	BlockEntityType<T> teType =
    			BlockEntityType.Builder.of(constructorSupplier, validBlocks).build(null);

    	teType.setRegistryName(registryName);

    	return teType;
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerTileEntity(
    		final IForgeRegistry<BlockEntityType<?>> registry, Class<T> teClass, Block... validBlocks) {
    	BlockEntityType<T> teType = createTeType(teClass, validBlocks);
    	registry.register(teType);
    	TileEntityHelper.registry = registry;
    	return teType;
    }

    private static class TileEntityConstructorSupplier<T extends BlockEntity> implements BlockEntitySupplier<T> {
    	private final Constructor<T> constructor;

		public TileEntityConstructorSupplier(Class<T> teClass) throws RuntimeException{
	        try {
	        	this.constructor = teClass.getConstructor(BlockPos.class, BlockState.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Failed to find the tileEntity constructor");
			}
		}

		@Override
		public T create(BlockPos pos, BlockState state) {
			try {
				return constructor.newInstance(pos, state);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
    }
}
