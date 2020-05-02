package rikka.librikka.container;

import java.util.List;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * The top level class must have a constructor with the following parameters for client side construction:
 * (int windowId, PlayerInventory inv, PacketBuffer data)
 */
public abstract class ContainerBase extends Container{
//	protected final ContainerType containerType;
	private List<IContainerListener> __listeners = null;
	
	protected ContainerBase(ContainerType containerType, int windowId) {
		super(containerType, windowId);
//		this.containerType = containerType;
	}

	protected ContainerBase(String namespace, int windowId) {
		super(null, windowId);
		
		ContainerType containerType = ContainerHelper.getContainerType(namespace, this.getClass());
//		this.containerType = containerType;
		ObfuscationReflectionHelper.setPrivateValue(Container.class, this, containerType, "containerType");
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

//	@Override
//	public ContainerType<?> getType() {
//		return this.containerType;
//	}
	
	/**
	 * @return Expose the previously accessible "listeners" field
	 */
	public List<IContainerListener> getListeners() {
        if (__listeners == null) {
        	__listeners = ObfuscationReflectionHelper.getPrivateValue(Container.class, this, "listeners");
        }
        
        return __listeners;
	}
}
