package rikka.librikka.model.loader;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import rikka.librikka.DirHorizontal8;

public class ModelGeometryBakeContext {
	public final IModelConfiguration owner;
	public final ModelBakery bakery;
	public final Function<Material, TextureAtlasSprite> spriteGetter;
	public final IModelTransform modelTransform;
	public final ItemOverrideList overrides;
	public final ResourceLocation modelLocation;
	public final Map<String, Material> loadedTextures;
	
	public ModelGeometryBakeContext(IModelConfiguration owner, ModelBakery bakery,
			Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
			ItemOverrideList overrides, ResourceLocation modelLocation,
			Map<String, Material> loadedTextures) {
		this.owner = owner;
		this.bakery = bakery;
		this.spriteGetter = spriteGetter;
		this.modelTransform = modelTransform;
		this.overrides = overrides;
		this.modelLocation = modelLocation;
		this.loadedTextures = loadedTextures;
	}
	
	public ModelGeometryBakeContext(ModelGeometryBakeContext parent) {
		this.owner = parent.owner;
		this.bakery = parent.bakery;
		this.spriteGetter = parent.spriteGetter;
		this.modelTransform = parent.modelTransform;
		this.overrides = parent.overrides;
		this.modelLocation = parent.modelLocation;
		this.loadedTextures = parent.loadedTextures;
	}

	public TextureAtlasSprite getTextureByKey(String name) {
		Material material = this.loadedTextures.get(name);
		return material == null ? null : this.spriteGetter.apply(material);
	}
	
	public TextureAtlasSprite getTexture(ResourceLocation resLoc) {
		Material material = this.loadedTextures.get("resloc#" + resLoc.toString());
		return material == null ? null : this.spriteGetter.apply(material);
	}

	public Function<ResourceLocation, TextureAtlasSprite> textureGetter() {
		return (resLoc)-> getTexture(resLoc);
	}

	public Direction getFacing() {
		return Direction.rotateFace(this.modelTransform.getRotation().getMatrix(), Direction.NORTH);
	}
	
	public DirHorizontal8 getFacing8(boolean offAxis) {
		DirHorizontal8 dir8 = DirHorizontal8.fromDirection4(getFacing());

		return offAxis ? dir8.clockwise() : dir8;
	}
}
