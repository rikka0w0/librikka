package rikka.librikka.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomContainerEventClientHanlder {
    @SideOnly(Side.CLIENT)
    void onDataArrivedFromServer(Object[] data);
}
