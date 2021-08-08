package rikka.librikka.container;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;

public abstract class ContainerNoInventory<HOST> extends ContainerBase {
    protected HOST host;

    /**
     * Server side constructor
     * @param host	owner BlockEntity
     * @param namespace modID
     * @param windowID windowID, just pass it
     */
    public ContainerNoInventory(@Nullable HOST host, String namespace, int windowID) {
    	super(namespace, windowID);
        this.host = host;
    }

    public ContainerNoInventory(@Nullable HOST host, MenuType<?> containerType, int windowID) {
    	super(containerType, windowID);
        this.host = host;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public abstract void broadcastChanges();
}
