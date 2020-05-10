package rikka.librikka.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventory<T extends IInventory> extends ContainerBase{
	protected int numOfSlots = 0;
	protected final int firstTileSlotID;
	
	protected final T inventoryTile;
	
	@Override
	protected Slot addSlot(Slot slotIn) {
		numOfSlots++;
		return super.addSlot(slotIn);
	}
	
	protected ContainerInventory(ContainerType containerType, int windowId, PlayerInventory invPlayer, T inventoryTile) {
		super(containerType, windowId);
		this.inventoryTile = inventoryTile;
		this.firstTileSlotID = populatePlayerInventory(invPlayer);
	}
	
	protected ContainerInventory(String namespace, int windowId, PlayerInventory invPlayer, T inventoryTile) {
		super(namespace, windowId);
		this.inventoryTile = inventoryTile;
		this.firstTileSlotID = populatePlayerInventory(invPlayer);
	}
	
	private int populatePlayerInventory(PlayerInventory invPlayer) {
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;
		final int HOTBAR_XPOS = 8;
		final int HOTBAR_YPOS = 142;
		
		// Add the players hotbar to the gui - the [xpos, ypos] location of each item
		for (int x = 0; x < 9; x++) {
			int slotNumber = x;
			addSlot(new Slot(invPlayer, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, 142));
		}
		
		final int PLAYER_INVENTORY_XPOS = 8;
		final int PLAYER_INVENTORY_YPOS = 84;
		// Add the rest of the players inventory to the gui
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				int slotNumber = 9 + y * 9 + x;
				int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
				int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
				addSlot(new Slot(invPlayer, slotNumber,  xpos, ypos));
			}
		}
		
		return numOfSlots;
	}
	
    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return inventoryTile.isUsableByPlayer(playerIn);
    }

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int sourceSlotIndex)
	{
		Slot sourceSlot = (Slot)inventorySlots.get(sourceSlotIndex);
		if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;  //EMPTY_ITEM
		ItemStack sourceStack = sourceSlot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();

		int inventorySize = numOfSlots - firstTileSlotID;
		
		// Check if the slot clicked is one of the vanilla container slots
		if (sourceSlotIndex >= 0 && sourceSlotIndex < firstTileSlotID) {
			// This is a vanilla container slot so merge the stack into the tile inventory
			if (!mergeItemStack(sourceStack, firstTileSlotID, firstTileSlotID + inventorySize , false)){
				return ItemStack.EMPTY;  // EMPTY_ITEM
			}
		} else if (sourceSlotIndex >= firstTileSlotID && sourceSlotIndex < firstTileSlotID + inventorySize) {
			// This is a TE slot so merge the stack into the players inventory
			if (!mergeItemStack(sourceStack, 0, firstTileSlotID, false)) {
				return ItemStack.EMPTY;   // EMPTY_ITEM
			}
		} else {
			System.err.print("Invalid slotIndex:" + sourceSlotIndex);
			return ItemStack.EMPTY;   // EMPTY_ITEM
		}

		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0) {  // getStackSize
			sourceSlot.putStack(ItemStack.EMPTY);  // EMPTY_ITEM
		} else {
			sourceSlot.onSlotChanged();
		}

		sourceSlot.onTake(player, sourceStack);  //onPickupFromSlot()
		return copyOfSourceStack;
	}
	
	@Override
	public void onContainerClosed(PlayerEntity playerIn)
	{
		super.onContainerClosed(playerIn);
		inventoryTile.closeInventory(playerIn);
	}
}
