package rikka.librikka.item;


import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public abstract class ItemBase extends Item {
    /**
     * @param name        Naming rules: lower case English letters and numbers only, words are separated by '_', e.g. "cooked_beef"
     * @param hasSubItems
     */
    public ItemBase(String name, Item.Properties properties) {
    	super(properties);
        setRegistryName(name);
        // localization key: item.<MODID>.<name>
    }
    
    public ItemBase(String name, CreativeModeTab group) {
    	this(name, new Item.Properties().tab(group));
    }
}
