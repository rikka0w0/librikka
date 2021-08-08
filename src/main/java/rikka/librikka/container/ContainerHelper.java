package rikka.librikka.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class ContainerHelper {
	private static IForgeRegistry<MenuType<?>> registry;
	public static String getRegistryName(Class<?> teClass) {
		String registryName = teClass.getName().toLowerCase().replace('$', '.');

		return registryName;
	}

	@SuppressWarnings("unchecked")
	public static <T extends AbstractContainerMenu> MenuType<T> getContainerType(String namespace, Class<T> containerClass) {
		String clsName = ContainerHelper.getRegistryName(containerClass);
		ResourceLocation res = new ResourceLocation(namespace, clsName);

		if (registry == null) {
			registry = RegistryManager.ACTIVE.getRegistry(MenuType.class);
		}

		return (MenuType<T>) registry.getValue(res);
	}

    /**
     * Create a MenuType and register it with a default registry name
     */
    public static <T extends AbstractContainerMenu> MenuType<T> createContainerType(Class<T> containerClass) {
    	String registryName = getRegistryName(containerClass);

    	ConstructorSupplier<T> constructorSupplier = new ConstructorSupplier<T>(containerClass);
		MenuType<T> containerType = new MenuType<>(constructorSupplier);
		containerType.setRegistryName(registryName);

    	return containerType;
    }

    public static <T extends AbstractContainerMenu> MenuType<T> register(
    		final IForgeRegistry<MenuType<?>> registry, Class<T> containerClass) {
    	MenuType<T> containerType = createContainerType(containerClass);
    	registry.register(containerType);
    	return containerType;
    }

    private static class ConstructorSupplier<T extends AbstractContainerMenu> implements MenuSupplier<T> {
    	private final Constructor<T> constructor;

		public ConstructorSupplier(Class<T> cClass) throws RuntimeException{
	        try {
	        	this.constructor = cClass.getConstructor(int.class, Inventory.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Failed to find the AbstractContainerMenu constructor");
			}
		}

		@Override
		public T create(int windowId, Inventory inv) {
			try {
				return constructor.newInstance(windowId, inv);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
    }
}
