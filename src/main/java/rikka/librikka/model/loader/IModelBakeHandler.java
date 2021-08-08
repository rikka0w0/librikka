package rikka.librikka.model.loader;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.event.TextureStitchEvent;

public interface IModelBakeHandler {
	/**
	 * Register textures
	 * 
	 *  if (!event.getMap().getTextureLocation().equals(TextureAtlas.LOCATION_BLOCKS_TEXTURE)) {
    		return;
    	}
    	
    	event.addSprite(new ResourceLocation())
	 * 
	 * @param event
	 */
	@Deprecated
	void onPreTextureStitchEvent(TextureStitchEvent.Pre event);
	@Deprecated
	BakedModel onModelBakeEvent();
}
