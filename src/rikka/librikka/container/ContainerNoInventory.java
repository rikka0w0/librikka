package rikka.librikka.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public abstract class ContainerNoInventory<HOST> extends Container {
    protected HOST host;

    public ContainerNoInventory(@Nullable Object host) {
        this.host = (HOST) host;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public abstract void detectAndSendChanges();
}
