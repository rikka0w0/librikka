package rikka.librikka.model.loader;

import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.common.model.IModelState;
import rikka.librikka.DirHorizontal8;
import rikka.librikka.model.Material;

public class ModelGeometryBakeContext {
	public final IModelConfiguration owner;
	public final ModelBakery bakery;
	public final Function<ResourceLocation, TextureAtlasSprite> spriteGetter;
	public final ItemOverrideList overrides;
	public final ResourceLocation modelLocation;
	public final Map<String, Material> loadedTextures;
	
	public ModelGeometryBakeContext(IModelConfiguration owner, ModelBakery bakery,
			Function<ResourceLocation, TextureAtlasSprite> spriteGetter,
			ItemOverrideList overrides, ResourceLocation modelLocation,
			Map<String, Material> loadedTextures) {
		this.owner = owner;
		this.bakery = bakery;
		this.spriteGetter = spriteGetter;
		this.overrides = overrides;
		this.modelLocation = modelLocation;
		this.loadedTextures = loadedTextures;
	}
	
	public ModelGeometryBakeContext(ModelGeometryBakeContext parent) {
		this.owner = parent.owner;
		this.bakery = parent.bakery;
		this.spriteGetter = parent.spriteGetter;
		this.overrides = parent.overrides;
		this.modelLocation = parent.modelLocation;
		this.loadedTextures = parent.loadedTextures;
	}

	public TextureAtlasSprite getTextureByKey(String name) {
		Material material = this.loadedTextures.get(name);
		return material == null ? null : this.spriteGetter.apply(material.texture);
	}
	
	public TextureAtlasSprite getTexture(ResourceLocation resLoc) {
		Material material = this.loadedTextures.get("resloc#" + resLoc.toString());
		return material == null ? null : this.spriteGetter.apply(material.texture);
	}

	public Function<ResourceLocation, TextureAtlasSprite> textureGetter() {
		return (resLoc)-> getTexture(resLoc);
	}

	public Direction getFacing() {
		IModelState modelState = this.owner.getCombinedState();
		if (modelState instanceof ModelRotation) {
			ModelRotation rotation = (ModelRotation) modelState;
			return rotation.rotateTransform(Direction.NORTH);
		}
		return Direction.NORTH;
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
