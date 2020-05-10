package rikka.librikka.multiblock;

import net.minecraftforge.client.model.data.ModelProperty;

public interface IMultiBlockTile {
	public static ModelProperty<IMultiBlockTile> prop  = new ModelProperty<>();
	
    MultiBlockTileInfo getMultiBlockTileInfo();

    /**
     * Called when a structure is being created (by a player), all tileEntity will receive this
     *
     * @param mbInfo
     */
    void onStructureCreating(MultiBlockTileInfo mbInfo);

    /**
     * Called when a structure is created by a player, all tileEntity will receive this
     *
     */
    void onStructureCreated();

    /**
     * Fired when the structure is destroyed (player/ explosion...)
     */
    void onStructureRemoved();
}
