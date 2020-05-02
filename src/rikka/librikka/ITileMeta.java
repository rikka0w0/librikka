package rikka.librikka;

import net.minecraft.tileentity.TileEntity;

public interface ITileMeta extends IMetaBase {
	Class<? extends TileEntity> teCls();
}
