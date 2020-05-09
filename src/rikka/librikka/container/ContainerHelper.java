package rikka.librikka.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;

public class ContainerHelper {
	private static IForgeRegistry registry;
	public static String getRegistryName(Class teClass) {
		String registryName = teClass.getName().toLowerCase().replace('$', '.');
		
		return registryName;
	}
	
	public static <T extends Container> ContainerType<T> getContainerType(String namespace, Class<T> containerClass) {
		String clsName = ContainerHelper.getRegistryName(containerClass);
		ResourceLocation res = new ResourceLocation(namespace, clsName);
		
		if (registry == null) {
			registry = GameRegistry.findRegistry(ContainerType.class);
		}
		
		return (ContainerType<T>) registry.getValue(res);
	}
	
    /**
     * Create a ContainerType and register it with a default registry name
     */
    public static <T extends Container> ContainerType<T> createContainerType(Class<T> containerClass) {
    	String registryName = getRegistryName(containerClass);
//    	registryName = registryName.substring(registryName.lastIndexOf(".") + 1);
//    	registryName = Essential.MODID + ":" + registryName;
    	// TODO: Check registryName
    	
    	ConstructorSupplier<T> constructorSupplier;
		try {
			constructorSupplier = new ConstructorSupplier<T>(containerClass);
		} catch (RuntimeException e) {
			return null;
		}
		
		ContainerType<T> containerType = new ContainerType(constructorSupplier);
    	    	
		containerType.setRegistryName(registryName);
    	
    	return containerType;
    }
    
    public static <T extends Container> ContainerType<T> register(
    		final IForgeRegistry<ContainerType<?>> registry, Class<T> containerClass) {
    	ContainerType<T> containerType = createContainerType(containerClass);
    	registry.register(containerType);
    	ContainerHelper.registry = registry;
    	return containerType;
    }
    
    private static class ConstructorSupplier<T extends Container> implements IContainerFactory<T> {
    	private final Constructor constructor;
    	
		public ConstructorSupplier(Class<T> cClass) throws RuntimeException{
	        try {
	        	this.constructor = cClass.getConstructor(int.class, PlayerInventory.class, PacketBuffer.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Failed to find the Container constructor");
			}
		}

		@Override
		public T create(int windowId, PlayerInventory inv, PacketBuffer data) {
			try {
				return (T) constructor.newInstance(windowId, inv, data);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
    }
}
