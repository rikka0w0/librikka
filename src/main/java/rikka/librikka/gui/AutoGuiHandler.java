package rikka.librikka.gui;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import rikka.librikka.container.ContainerHelper;


/**
 * A more automatic and object-oriented GuiHandler, ID 0 to 5 is reserved for EnumFacing, and ID>5 will be considered as custom Gui/Container
 *
 */
public class AutoGuiHandler {
	@OnlyIn(Dist.CLIENT)
	public static <TC extends Container, TS extends ContainerScreen<? extends TC>> 
	void registerContainerGui(Class<TC> containerClass) {
		registerContainerGui(ModLoadingContext.get().getActiveNamespace(), containerClass);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static <TC extends Container, TS extends ContainerScreen<? extends TC>> 
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
	public static <TC extends Container, TS extends ContainerScreen<? extends TC>> 
	void registerContainerGui(String namespace, Class<TC> containerClass, Class<TS> screenClass) {
		ContainerType<TC> containerType = ContainerHelper.getContainerType(namespace, containerClass);
		ScreenManager.registerFactory(containerType, new ConstructorSupplier(containerClass, screenClass));
	}
	
	private static class ConstructorSupplier<TC extends Container, TS extends ContainerScreen<? extends TC>> 
		implements ScreenManager.IScreenFactory{
    	private final Constructor<? extends TS> constructor;
    	
		public ConstructorSupplier(Class<TC> containerClass, Class<TS> screenClass) throws RuntimeException{
	        try {
	        	this.constructor = screenClass.getConstructor(containerClass, PlayerInventory.class, ITextComponent.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Failed to find the ContainerScreen constructor");
			}
		}

		@Override
		public Screen create(Container container, PlayerInventory inv, ITextComponent text) {
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
		Class<? extends ContainerScreen> value();
	}
}
