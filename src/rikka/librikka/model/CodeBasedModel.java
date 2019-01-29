package rikka.librikka.model;

import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.model.loader.EasyTextureLoader;

import java.util.Collection;
import java.util.Set;

/**
 * An alternative to ISBRH in 1.7.10 and previous
 *
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public abstract class CodeBasedModel implements IModel, IBakedModel {
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
        ResourceLocation resLoc = new ResourceLocation(texture);
        this.textures.add(resLoc);
        return resLoc;
    }
    
    protected ResourceLocation registerTexture(ResourceLocation resLoc) {
        this.textures.add(resLoc);
        return resLoc;
    }

    protected abstract void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry);

    ////////////////
    /// IModel
    ////////////////
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableSet.copyOf(this.textures);
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format,
                            Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
    	EasyTextureLoader.applyTextures(this, CodeBasedModel.class, bakedTextureGetter);
        bake(bakedTextureGetter);
        return this;
    }

    /////////////////
    /// IBakedModel
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
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
