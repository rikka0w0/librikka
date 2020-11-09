package rikka.librikka.model.loader;

import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import rikka.librikka.DirHorizontal8;

public class ModelGeometryBakeContext {
	public final IModelConfiguration owner;
	public final ModelBakery bakery;
	public final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
	public final IModelTransform modelTransform;
	public final ItemOverrideList overrides;
	public final ResourceLocation modelLocation;
	public final Map<String, RenderMaterial> loadedTextures;
	
	public ModelGeometryBakeContext(IModelConfiguration owner, ModelBakery bakery,
			Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
			ItemOverrideList overrides, ResourceLocation modelLocation,
			Map<String, RenderMaterial> loadedTextures) {
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
		RenderMaterial material = this.loadedTextures.get(name);
		return material == null ? null : this.spriteGetter.apply(material);
	}
	
	public TextureAtlasSprite getTexture(ResourceLocation resLoc) {
		RenderMaterial material = this.loadedTextures.get("resloc#" + resLoc.toString());
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
	
	public static Pair<Integer, Boolean> encodeDirection(DirHorizontal8 dir) {
		return Pair.of(
				((dir.ordinal()&7)>>1) * 90, 
				dir != DirHorizontal8.fromDirection4(dir.toDirection4())
				);
	}
	
	public static int encodeDirection(Direction dir) {
		return encodeDirection(DirHorizontal8.fromDirection4(dir)).getLeft();
	}
}
