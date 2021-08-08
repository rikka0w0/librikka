package rikka.librikka.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ItemBlockBase extends BlockItem {
	/**
	 * This is the only valid constructor for ItemBlockBase, if to be used with BlockBase
	 * Your own implementation should keep this constructor!
	 * @param block
	 * @param props
	 */
    public ItemBlockBase(Block block, Item.Properties props) {
    	super(block, props);
        
        this.setRegistryName(block.getRegistryName());
        // localization key: item.<MODID>.<name>
    }
}
