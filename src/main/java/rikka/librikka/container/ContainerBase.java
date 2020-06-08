package rikka.librikka.container;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * The top level class must have a constructor with the following parameters for client side construction:
 * (int windowId, PlayerInventory inv, PacketBuffer data)
 */
public abstract class ContainerBase extends Container {
	protected final ContainerType<?> containerType;
	private List<IContainerListener> __listeners = null;
	
	protected ContainerBase(ContainerType<?> containerType, int windowId) {
		super(containerType, windowId);
		this.containerType = containerType;
	}

	protected ContainerBase(String namespace, int windowId) {
		super(null, windowId);
		
		ContainerType<?> containerType = ContainerHelper.getContainerType(namespace, this.getClass());
		this.containerType = containerType;
//		ObfuscationReflectionHelper.setPrivateValue(Container.class, this, containerType, "containerType");
//		for (Field f:Container.class.getDeclaredFields()) {
//			if (f.getType() == ContainerType.class) {
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
	public ContainerType<?> getType() {
		return this.containerType;
	}
	
	/**
	 * @return Expose the previously accessible "listeners" field
	 */
	@SuppressWarnings("unchecked")
	public List<IContainerListener> getListeners() {
        if (__listeners == null) {
        	Field listenersField = null;
        	try {
        		listenersField = ObfuscationReflectionHelper.findField(Container.class, "field_75149_d");
        	
        	} catch (Exception e) {
        		listenersField = null;
        	}
        	
        	if (listenersField == null) {
        		for (Field f:Container.class.getDeclaredFields()) {
        			if (f.getType() == List.class) {
	    				try {
	    					f.setAccessible(true);
	    					__listeners = (List<IContainerListener>) f.get(this);
	    					f.setAccessible(false);
	    					break;
	    				} catch (IllegalArgumentException | IllegalAccessException e) {
	    					e.printStackTrace();
	    				}
        			}	
        		} 
        	} else {
        		try {
					__listeners = (List<IContainerListener>) listenersField.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
        	}
        }
        	
        return __listeners;
	}
}
