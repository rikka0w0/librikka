package rikka.librikka.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ItemBlockBase extends BlockItem {
	public static interface Constructor {
		ItemBlockBase create(Block block, Item.Properties props);
	}

    public ItemBlockBase(Block block, Item.Properties props) {
    	super(block, props);

        this.setRegistryName(block.getRegistryName());
        // localization key: item.<MODID>.<name>
    }
}
