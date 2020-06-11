package rikka.librikka.model;

import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.loader.IModelBakeHandler;
import rikka.librikka.model.loader.ModelGeometryBakeContext;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An alternative to ISBRH in 1.7.10 and previous
 *
 * @author Rikka0_0
 */

public abstract class CodeBasedModel implements IDynamicBakedModel, IModelBakeHandler {
	public final static List<BakedQuad> emptyQuadList = ImmutableList.of();

    ////////////////////////////////////////////////////////////////////////
    private final Map<ResourceLocation, Field> textures = new HashMap<>();

    protected CodeBasedModel() {
    	if (!skipLegacyTextureRegistration()) {
    		EasyTextureLoader.foreachMarker(this.getClass(), CodeBasedModel.class, (cls, field)-> {
    			String textureName = EasyTextureLoader.getMarkerValue(field);

    			// Skip all keys as they are not supported in the legacy texture registration scheme
    			if (!textureName.startsWith("#"))
    				this.textures.put(registerTexture(textureName), field);

    		});
    	}
    }
    
    protected boolean skipLegacyTextureRegistration() {
    	return false;
    }

    /**
     * @param texture file path, including domain
     * @return a key which can be used to retrieve the corresponding TextureAtlasSprite (like IIcon)
     */
    protected ResourceLocation registerTexture(String texture) {
    	return registerTexture(new ResourceLocation(texture));
    }

    protected ResourceLocation registerTexture(String namespace, String texture) {
        return registerTexture(new ResourceLocation(namespace, texture));
    }
    
    protected ResourceLocation registerTexture(ResourceLocation resLoc) {
        this.textures.put(resLoc, null);
        return resLoc;
    }

    protected abstract void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry);

    /**
     * To be called by {@link rikka.librikka.model.loader.ModelGeometryWrapper}
     * @param context An instance of {@link rikka.librikka.model.loader.ModelGeometryBakeContext}
     * @return the bakedmodel, usually be the current instance
     */
    public IBakedModel bake(ModelGeometryBakeContext context) {
    	this.bake(context.textureGetter());
    	return this;
    }

    /////////////////
    /// IModelBakeHandler, a temp replacement of 1.12.2 IModel
    /////////////////
	protected ResourceLocation atlasLocation() {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}

    @Override
    public final void onPreTextureStitchEvent(TextureStitchEvent.Pre event) {
    	if (!EasyTextureLoader.isBlockAtlas(event))
    		return;

    	for(ResourceLocation res: this.textures.keySet()) {
    		event.addSprite(res);
    	}
    }

    @Override
    public final IBakedModel onModelBakeEvent() {
    	Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter = 
    			EasyTextureLoader.blockTextureGetter();
    	
    	this.textures.forEach((resLoc, field)-> {
    		if (field != null)
    			EasyTextureLoader.applyTexture(this, field, bakedTextureGetter.apply(resLoc));
    		}
    	);
    	
        bake(bakedTextureGetter);
        return this;
    }

    /////////////////
    /// IDynamicBakedModel, was IBakedModel
    /////////////////
	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
