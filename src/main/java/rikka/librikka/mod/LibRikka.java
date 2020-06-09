package rikka.librikka.mod;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(LibRikka.MODID)
public class LibRikka {
	public final static String MODID = "librikka";
	
	public static CommonProxy proxy = DistExecutor.runForDist(()->ClientProxy::new, ()->CommonProxy::new);
	
	public LibRikka() {
		proxy.registerModelLoaders();
	}
}
