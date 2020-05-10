package rikka.librikka.model;

import java.util.function.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import rikka.librikka.model.loader.EasyTextureLoader;
import rikka.librikka.model.loader.IModelBakeHandler;

import java.util.Collection;
import java.util.Set;

/**
 * An alternative to ISBRH in 1.7.10 and previous
 *
 * @author Rikka0_0
 */

public abstract class CodeBasedModel implements IDynamicBakedModel, IModelBakeHandler {
    ////////////////////////////////////////////////////////////////////////
    private final Set<ResourceLocation> textures = Sets.newHashSet();

    protected CodeBasedModel() {
    	EasyTextureLoader.registerTextures(this, CodeBasedModel.class, textures);
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
        this.textures.add(resLoc);
        return resLoc;
    }

    protected abstract void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry);

    ////////////////
    /// Was IModel
    ////////////////
    public final Collection<ResourceLocation> getTextures() {
        return ImmutableSet.copyOf(this.textures);
    }

    @Override
    public final void onPreTextureStitchEvent(TextureStitchEvent.Pre event) {
    	if (!EasyTextureLoader.isBlockAtlas(event))
    		return;

    	for(ResourceLocation res: this.textures) {
    		event.addSprite(res);
    	}
    }

    @Override
    public final IBakedModel onModelBakeEvent() {
    	Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter = 
    			Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
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
