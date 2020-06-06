package rikka.librikka.model;

import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.loader.IModelBakeHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An alternative to ISBRH in 1.7.10 and previous
 *
 * @author Rikka0_0
 */

public abstract class CodeBasedModel implements IDynamicBakedModel, IModelGeometry<CodeBasedModel>, IModelBakeHandler {
    ////////////////////////////////////////////////////////////////////////
    private final Map<ResourceLocation, Material> textures = new HashMap<>();

	protected CodeBasedModel() {
		Set<ResourceLocation> annotatedTextures = new HashSet<>();
		EasyTextureLoader.registerTextures(this, CodeBasedModel.class, annotatedTextures);
		for (ResourceLocation resLoc : annotatedTextures)
			registerTexture(resLoc);
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
        this.textures.put(resLoc, new Material(atlasLocation(), resLoc));
        return resLoc;
    }

    protected abstract void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry);

    ////////////////
    /// IModelGeometry, was IModel
    ////////////////
	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery,
			Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
			ItemOverrideList overrides, ResourceLocation modelLocation) {

    	Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter = 
    			(resLoc)-> spriteGetter.apply(textures.get(resLoc));
    	EasyTextureLoader.applyTextures(this, CodeBasedModel.class, bakedTextureGetter);
        bake(bakedTextureGetter);
        return this;
	}

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return textures.values();
	}
	
	protected ResourceLocation atlasLocation() {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}

    @Override
    public final void onPreTextureStitchEvent(TextureStitchEvent.Pre event) {
    	if (!event.getMap().getTextureLocation().equals(atlasLocation()))
    		return;

    	for(ResourceLocation res: this.textures.keySet()) {
    		event.addSprite(res);
    	}
    }

    @Override
    public final IBakedModel onModelBakeEvent() {
    	Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter = 
    			Minecraft.getInstance().getAtlasSpriteGetter(atlasLocation());
    	EasyTextureLoader.applyTextures(this, CodeBasedModel.class, bakedTextureGetter);
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
	public boolean func_230044_c_() {	// diffuselighting
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
