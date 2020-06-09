package rikka.librikka.mod;

import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy {
	@Override
    public void registerModelLoaders() {
    	// Need this to prevent crash during data generation
    	if (Minecraft.getInstance()==null || Minecraft.getInstance().getResourceManager() == null)
    		return;

    	ClientRegistrationHandler.registerModelLoaders();
    }
}
