package rikka.librikka.tileentity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class TileEntityHelper {
	private static IForgeRegistry<?> registry;
	public static String getRegistryName(Class<?> teClass) {
		String registryName = teClass.getName().toLowerCase().replace('$', '.');
		
		return registryName;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> TileEntityType<T> getTeType(String namespace, Class<T> teClass) {
		String clsName = TileEntityHelper.getRegistryName(teClass);
		ResourceLocation res = new ResourceLocation(namespace, clsName);
		
		if (registry == null) {
			registry = GameRegistry.findRegistry(TileEntityType.class);
		}
		
		return (TileEntityType<T>) registry.getValue(res);
	}
	
	
    /**
     * Create a TileEntityType and register it with a default registry name
     */
    public static <T extends TileEntity> TileEntityType<T> createTeType(Class<T> teClass, Block... validBlocks) {
    	String registryName = getRegistryName(teClass);
//    	registryName = registryName.substring(registryName.lastIndexOf(".") + 1);
//    	registryName = Essential.MODID + ":" + registryName;
    	// TODO: Check registryName
    	
    	TileEntityConstructorSupplier<T> constructorSupplier;
		try {
			constructorSupplier = new TileEntityConstructorSupplier<T>(teClass);
		} catch (RuntimeException e) {
			return null;
		}
		
    	TileEntityType<T> teType = 
    			TileEntityType.Builder.create(constructorSupplier, validBlocks).build(null);  
    	// TODO: What is a datafixer?
    	    	
    	teType.setRegistryName(registryName);
    	
    	return teType;
    }
    
    public static <T extends TileEntity> TileEntityType<T> registerTileEntity(
    		final IForgeRegistry<TileEntityType<?>> registry, Class<T> teClass, Block... validBlocks) {
    	TileEntityType<T> teType = createTeType(teClass, validBlocks);
    	registry.register(teType);
    	TileEntityHelper.registry = registry;
    	return teType;
    }
    
    private static class TileEntityConstructorSupplier<T extends TileEntity> implements Supplier<T> {
    	private final Constructor<T> constructor;
    	
		public TileEntityConstructorSupplier(Class<T> teClass) throws RuntimeException{
	        try {
	        	this.constructor = teClass.getConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Failed to find the tileEntity constructor");
			}
		}
    	
    	@Override
		public T get() {
			try {
				return constructor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
    }
}
