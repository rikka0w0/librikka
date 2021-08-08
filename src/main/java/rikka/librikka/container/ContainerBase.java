package rikka.librikka.container;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * The top level class must have a constructor with the following parameters for client side construction:
 * (int windowId, Inventory inv)
 */
public abstract class ContainerBase extends AbstractContainerMenu {
	protected final MenuType<?> containerType;
	private List<ContainerListener> __listeners = null;

	protected ContainerBase(MenuType<?> containerType, int windowId) {
		super(containerType, windowId);
		this.containerType = containerType;
	}

	protected ContainerBase(String namespace, int windowId) {
		super(null, windowId);

		MenuType<?> containerType = ContainerHelper.getContainerType(namespace, this.getClass());
		this.containerType = containerType;
//		ObfuscationReflectionHelper.setPrivateValue(Container.class, this, containerType, "containerType");
//		for (Field f:Container.class.getDeclaredFields()) {
//			if (f.getType() == MenuType.class) {
//				try {
//					f.setAccessible(true);
//					f.set(this, containerType);
//					f.setAccessible(false);
//					break;
//				} catch (IllegalArgumentException | IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}

	@Override
	public MenuType<?> getType() {
		return this.containerType;
	}

	/**
	 * @return Expose the previously accessible "listeners" field
	 */
	@SuppressWarnings("unchecked")
	public List<ContainerListener> getListeners() {
        if (__listeners == null) {
        	Field listenersField = null;
        	try {
        		listenersField = ObfuscationReflectionHelper.findField(AbstractContainerMenu.class, "containerListeners");

        	} catch (Exception e) {
        		listenersField = null;
        	}

        	if (listenersField == null) {
        		for (Field f:AbstractContainerMenu.class.getDeclaredFields()) {
        			if (f.getType() == List.class) {
	    				try {
	    					f.setAccessible(true);
	    					__listeners = (List<ContainerListener>) f.get(this);
	    					f.setAccessible(false);
	    					break;
	    				} catch (IllegalArgumentException | IllegalAccessException e) {
	    					e.printStackTrace();
	    				}
        			}
        		}
        	} else {
        		try {
					__listeners = (List<ContainerListener>) listenersField.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
        	}
        }

        return __listeners;
	}
}
