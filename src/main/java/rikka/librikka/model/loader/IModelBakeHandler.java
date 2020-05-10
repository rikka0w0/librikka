package rikka.librikka.model.loader;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.client.event.TextureStitchEvent;

public interface IModelBakeHandler {
	/**
	 * Register textures
	 * 
	 *  if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
    		return;
    	}
    	
    	event.addSprite(new ResourceLocation())
	 * 
	 * @param event
	 */
	void onPreTextureStitchEvent(TextureStitchEvent.Pre event);
	IBakedModel onModelBakeEvent();
}
