package rikka.librikka.container;

import java.util.Arrays;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.Component;

public class StandardInventory implements Container{
	private final ItemStack[] itemStacks;
	private final BlockEntity ownerTile;
	
	public boolean hasCustomName = false;
	public double validRange = 8;
	
	public StandardInventory(BlockEntity ownerTile, int size) {
		this.ownerTile = ownerTile;
		this.itemStacks = new ItemStack[size];
		
		clearContent();
	}
	
	public void readFromNBT(CompoundTag nbt) {
		final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType() for a listing
		ListTag dataForAllSlots = nbt.getList("items", NBT_TYPE_COMPOUND);

		Arrays.fill(itemStacks, ItemStack.EMPTY);           // set all slots to empty EMPTY_ITEM
		for (int i = 0; i < dataForAllSlots.size(); ++i) {
			CompoundTag dataForOneSlot = dataForAllSlots.getCompound(i);
			int slotIndex = dataForOneSlot.getByte("slot") & 255;

			if (slotIndex >= 0 && slotIndex < itemStacks.length) {
				itemStacks[slotIndex] = ItemStack.of(dataForOneSlot);
			}
		}
	}
	
	public void writeToNBT(CompoundTag nbt)	{
		ListTag dataForAllSlots = new ListTag();
		for (int i = 0; i < itemStacks.length; ++i) {
			if (!itemStacks[i].isEmpty())	{ //isEmpty()
				CompoundTag dataForThisSlot = new CompoundTag();
				dataForThisSlot.putByte("slot", (byte) i);
				itemStacks[i].save(dataForThisSlot);
				dataForAllSlots.add(dataForThisSlot);
			}
		}
		nbt.put("items", dataForAllSlots);
	}
	
	////////////////////////
	/// Container
	////////////////////////
	@Override
	public void setChanged() {
		ownerTile.setChanged();
	}
	
	@Override
	public int getMaxStackSize() {
		return 64;
	}
	
	@Override
	public int getContainerSize() {
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
	public ItemStack getItem(int slotIndex) {
		return itemStacks[slotIndex];
	}

	@Override
	public void setItem(int slotIndex, ItemStack itemstack) {
		itemStacks[slotIndex] = itemstack;
		if (!itemstack.isEmpty() && itemstack.getCount() > getMaxStackSize()) { 
			itemstack.setCount(getMaxStackSize());
		}
		setChanged();
	}
	
	@Override
	public ItemStack removeItem(int slotIndex, int count) {
		ItemStack itemStackInSlot = getItem(slotIndex);
		if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;  //isEmpty(), EMPTY_ITEM
		
		ItemStack itemStackRemoved;
		if (itemStackInSlot.getCount() <= count) { //getStackSize
			itemStackRemoved = itemStackInSlot;
			setItem(slotIndex, ItemStack.EMPTY); // EMPTY_ITEM
		} else {
			itemStackRemoved = itemStackInSlot.split(count);
			if (itemStackInSlot.getCount() == 0) { //getStackSize
				setItem(slotIndex, ItemStack.EMPTY); //EMPTY_ITEM
			}
		}
		setChanged();
		return itemStackRemoved;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slotIndex) {
		ItemStack itemStack = getItem(slotIndex);
		if (!itemStack.isEmpty())
			setItem(slotIndex, ItemStack.EMPTY);
		return itemStack;
	}

	@Override
	public void clearContent() {
		Arrays.fill(itemStacks, ItemStack.EMPTY);  //EMPTY_ITEM
	}

	@Override
	public boolean stillValid(Player player) {
		BlockPos pos = ownerTile.getBlockPos();
		if (ownerTile.getLevel().getBlockEntity(pos) != ownerTile)
			return false;
		
		return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < validRange*validRange;
	}
	
	
	
	
	
	@Override
	public void startOpen(Player player) {}

	@Override
	public void stopOpen(Player player) {}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return false;
	}
}
