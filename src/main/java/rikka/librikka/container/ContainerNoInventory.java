package rikka.librikka.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;

public abstract class ContainerNoInventory<HOST> extends ContainerBase {
    protected HOST host;

    /**
     * Server side constructor
     * @param host	owner TileEntity
     * @param namespace modID
     * @param windowID windowID, just pass it
     */
    public ContainerNoInventory(@Nullable HOST host, String namespace, int windowID) {
    	super(namespace, windowID);
        this.host = host;
    }
    
    public ContainerNoInventory(@Nullable HOST host, ContainerType<?> containerType, int windowID) {
    	super(containerType, windowID);
        this.host = host;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    @Override
    public abstract void detectAndSendChanges();
}
