package rikka.librikka.gui;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import rikka.librikka.container.ContainerHelper;


/**
 * A more powerful GuiHandler, automate AbstractContainerScreen-AbstractContainerMenu registration
 */
public class AutoGuiHandler {
	@OnlyIn(Dist.CLIENT)
	public static <TC extends AbstractContainerMenu, TS extends AbstractContainerScreen<? extends TC>>
	void registerContainerGui(Class<TC> containerClass) {
		registerContainerGui(ModLoadingContext.get().getActiveNamespace(), containerClass);
	}

	@SuppressWarnings("unchecked")
	@OnlyIn(Dist.CLIENT)
	public static <TC extends AbstractContainerMenu, TS extends AbstractContainerScreen<TC>>
	void registerContainerGui(String namespace, Class<TC> containerClass) {
		Class<TS> screenClass;
		if (containerClass.isAnnotationPresent(Marker.class)) {
			screenClass = (Class<TS>) containerClass.getAnnotation(Marker.class).value();
			registerContainerGui(namespace, containerClass, screenClass);
		} else {
			throw new RuntimeException(containerClass.getName() + "does not have the marker!");
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static <TC extends AbstractContainerMenu, TS extends AbstractContainerScreen<TC>>
	void registerContainerGui(String namespace, Class<TC> containerClass, Class<TS> screenClass) {
		MenuType<TC> containerType = ContainerHelper.getContainerType(namespace, containerClass);
		MenuScreens.register(containerType, new ConstructorSupplier<TC, TS>(containerClass, screenClass));
	}

	private static class ConstructorSupplier<TC extends AbstractContainerMenu, TS extends AbstractContainerScreen<TC>>
		implements MenuScreens.ScreenConstructor<TC, TS> {
    	private final Constructor<? extends TS> constructor;

		public ConstructorSupplier(Class<TC> containerClass, Class<TS> screenClass) throws RuntimeException{
	        try {
	        	this.constructor = screenClass.getConstructor(containerClass, Inventory.class, Component.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Failed to find the AbstractContainerScreen constructor");
			}
		}

		@Override
		public TS create(AbstractContainerMenu container, Inventory inv, Component text) {
			try {
				return constructor.newInstance(container, inv, text);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface Marker {
		Class<? extends AbstractContainerScreen<?>> value();
	}
}
