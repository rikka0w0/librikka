package rikka.librikka.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ICustomContainerEventClientHanlder {
	@OnlyIn(Dist.CLIENT)
    void onDataArrivedFromServer(Object[] data);
}
