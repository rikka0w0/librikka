package rikka.librikka.container;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * The top level class must have a constructor with the following parameters for client side construction:
 * (int windowId, Inventory inv)
 */
public abstract class ContainerBase extends AbstractContainerMenu {
	protected final MenuType<?> containerType;

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
}
