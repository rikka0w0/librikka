package rikka.librikka.model;

import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;
import java.util.Random;

/**
 * An invisible model
 *
 * @author Rikka0_0
 */
@OnlyIn(Dist.CLIENT)
public class GhostModel extends CodeBasedModel {
    private final ResourceLocation texture;
    private TextureAtlasSprite loadedTexture;

    public GhostModel() {
        texture = this.registerTexture("minecraft:blocks/iron_block");
    }
    
    public GhostModel(String particleTexture) {
        texture = this.registerTexture(particleTexture);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        return emptyQuadList;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.loadedTexture;
    }

    @Override
    protected void bake(Function<ResourceLocation, TextureAtlasSprite> registry) {
        loadedTexture = registry.apply(this.texture);
    }
}
