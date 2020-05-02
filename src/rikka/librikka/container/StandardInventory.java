package rikka.librikka.container;

import java.util.Arrays;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.ITextComponent;

public class StandardInventory implements IInventory{
	private final ItemStack[] itemStacks;
	public final String name;
	private final TileEntity ownerTile;
	
	public boolean hasCustomName = false;
	public double validRange = 8;
	
	public StandardInventory(TileEntity ownerTile, int size, String name) {
		this.ownerTile = ownerTile;
		this.itemStacks = new ItemStack[size];
		this.name = name;
		
		clear();
	}
	
	public void readFromNBT(CompoundNBT nbt) {
		final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType() for a listing
		ListNBT dataForAllSlots = nbt.getList("items", NBT_TYPE_COMPOUND);

		Arrays.fill(itemStacks, ItemStack.EMPTY);           // set all slots to empty EMPTY_ITEM
		for (int i = 0; i < dataForAllSlots.size(); ++i) {
			CompoundNBT dataForOneSlot = dataForAllSlots.getCompound(i);
			int slotIndex = dataForOneSlot.getByte("slot") & 255;

			if (slotIndex >= 0 && slotIndex < itemStacks.length) {
				itemStacks[slotIndex] = ItemStack.read(dataForOneSlot);
			}
		}
	}
	
	public void writeToNBT(CompoundNBT nbt)	{
		ListNBT dataForAllSlots = new ListNBT();
		for (int i = 0; i < itemStacks.length; ++i) {
			if (!itemStacks[i].isEmpty())	{ //isEmpty()
				CompoundNBT dataForThisSlot = new CompoundNBT();
				dataForThisSlot.putByte("slot", (byte) i);
				itemStacks[i].write(dataForThisSlot);
				dataForAllSlots.add(dataForThisSlot);
			}
		}
		nbt.put("items", dataForAllSlots);
	}
	
	////////////////////////
	/// IInventory
	////////////////////////
//	@Override
//	public ITextComponent getDisplayName() {
//		return null;
//	}

	@Override
	public void markDirty() {
		ownerTile.markDirty();
	}
	
//	@Override
//	public String getName() {
//		return name;
//	}
//
//	@Override
//	public boolean hasCustomName() {
//		return hasCustomName;
//	}

	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	
	@Override
	public int getSizeInventory() {
		return itemStacks.length;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : itemStacks) {
			if (!itemstack.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		return itemStacks[slotIndex];
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
		itemStacks[slotIndex] = itemstack;
		if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit()) { 
			itemstack.setCount(getInventoryStackLimit());
		}
		markDirty();
	}
	
	@Override
	public ItemStack decrStackSize(int slotIndex, int count) {
		ItemStack itemStackInSlot = getStackInSlot(slotIndex);
		if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;  //isEmpty(), EMPTY_ITEM
		
		ItemStack itemStackRemoved;
		if (itemStackInSlot.getCount() <= count) { //getStackSize
			itemStackRemoved = itemStackInSlot;
			setInventorySlotContents(slotIndex, ItemStack.EMPTY); // EMPTY_ITEM
		} else {
			itemStackRemoved = itemStackInSlot.split(count);
			if (itemStackInSlot.getCount() == 0) { //getStackSize
				setInventorySlotContents(slotIndex, ItemStack.EMPTY); //EMPTY_ITEM
			}
		}
		markDirty();
		return itemStackRemoved;
	}

	@Override
	public ItemStack removeStackFromSlot(int slotIndex) {
		ItemStack itemStack = getStackInSlot(slotIndex);
		if (!itemStack.isEmpty())
			setInventorySlotContents(slotIndex, ItemStack.EMPTY);
		return itemStack;
	}

	@Override
	public void clear() {
		Arrays.fill(itemStacks, ItemStack.EMPTY);  //EMPTY_ITEM
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		BlockPos pos = ownerTile.getPos();
		if (ownerTile.getWorld().getTileEntity(pos) != ownerTile)
			return false;
		
		return player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < validRange*validRange;
	}
	
	
	
	
	
	@Override
	public void openInventory(PlayerEntity player) {}

	@Override
	public void closeInventory(PlayerEntity player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

//	@Override
//	public int getField(int id) {
//		return 0;
//	}
//
//	@Override
//	public void setField(int id, int value) {
//		
//	}
//
//	@Override
//	public int getFieldCount() {
//		return 0;
//	}
}
