package rikka.librikka.model.loader;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import rikka.librikka.model.CodeBasedModel;

public class ModelGeometryWrapper implements IModelGeometry<ModelGeometryWrapper> {
	protected final Map<String, RenderMaterial> textures = new HashMap<>();
	protected final Function<ModelGeometryBakeContext, IBakedModel> bakedModelSupplier;
	protected final Map<Field, String> textureFields = new HashMap<>();
	
	@SuppressWarnings("deprecation")
	public ModelGeometryWrapper(
			@Nullable JsonObject textureJsonObj, 
			@Nullable Class<?> textureSupplier,
			Function<ModelGeometryBakeContext, IBakedModel> bakedModelSupplier) {
		this(textureJsonObj, textureSupplier, CodeBasedModel.class, 
				AtlasTexture.LOCATION_BLOCKS_TEXTURE, bakedModelSupplier);
	}
	
	/**
	 * An implementation of MinecraftForge's IModelGeometry, for easier dynamic model loading
	 * @param textureJsonObj A Json Object consists of TextureKey-ResourceLocation pairs
	 * @param textureSupplier {@link rikka.librikka.model.loader.Mark}
	 * @param scanEndClass if textureSupplier is not null, this field indicates 
	 * when {@link rikka.librikka.model.loader.Mark} scan stops
	 * @param atlasLoc the location of the texture atlas
	 * @param bakedModelSupplier A functional interface which returns an instance of a IBakedModel
	 */
	public ModelGeometryWrapper(
			@Nullable JsonObject textureJsonObj, 
			@Nullable Class<?> textureSupplier,
			Class<?> scanEndClass,
			ResourceLocation atlasLoc,
			Function<ModelGeometryBakeContext, IBakedModel> bakedModelSupplier) {
		this.bakedModelSupplier = bakedModelSupplier;
		
		if (textureJsonObj != null) {
			for (Entry<String, JsonElement> entry: textureJsonObj.entrySet()) {
				String key = entry.getKey();
				String textureLoc = entry.getValue().getAsString();
				ResourceLocation textureResLoc = new ResourceLocation(textureLoc);
				this.textures.put(key, new RenderMaterial(atlasLoc, textureResLoc));
			}
		}
		
		if (textureSupplier != null) {
			EasyTextureLoader.foreachMarker(textureSupplier, scanEndClass, (cls, field)->{
				String textureName = EasyTextureLoader.getMarkerValue(field);
				if (!textureName.startsWith("#")) {
					String key = "resloc#" + textureName.toString();
					ResourceLocation textureResLoc = new ResourceLocation(textureName);
					this.textures.put(key, new RenderMaterial(atlasLoc, textureResLoc));
				}
				this.textureFields.put(field, textureName);
			});
		}
	}

	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery,
			Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
			ItemOverrideList overrides, ResourceLocation modelLocation) {

		final ModelGeometryBakeContext context = new ModelGeometryBakeContext(
				owner, bakery, spriteGetter, modelTransform, overrides, modelLocation,
				this.textures);

		final IBakedModel model = bakedModelSupplier.apply(context);
		
		if (model instanceof CodeBasedModel) {
			this.textureFields.forEach((field, textureName)->{
				TextureAtlasSprite texture;
	    		if (textureName.startsWith("#")) {
	    			texture = context.getTextureByKey(textureName.substring(1));
	    		} else {
	    			texture = context.getTexture(new ResourceLocation(textureName));
	    		}
	    		EasyTextureLoader.applyTexture(model, field, texture);
			});

			return ((CodeBasedModel) model).bake(context);
		}

		return model;
	}

	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner,
			Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return this.textures.values();
	}
}
