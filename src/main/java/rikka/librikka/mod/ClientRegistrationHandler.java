package rikka.librikka.mod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import rikka.librikka.model.GeneratedModelLoader;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = LibRikka.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistrationHandler {
	public static void registerModelLoaders() {
		ModelLoaderRegistry.registerLoader(GeneratedModelLoader.id, GeneratedModelLoader.instance);
	}
}
