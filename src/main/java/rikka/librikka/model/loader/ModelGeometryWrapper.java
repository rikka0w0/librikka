package rikka.librikka.model.loader;

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
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import rikka.librikka.model.CodeBasedModel;

public class ModelGeometryWrapper implements IModelGeometry<ModelGeometryWrapper> {
	protected final Map<String, Material> textures;
	protected final Function<ModelGeometryBakeContext, IBakedModel> bakedModelSupplier;
	
	/**
	 * An implementation of MinecraftForge's IModelGeometry, for easier dynamic model loading
	 * @param textureJsonObj A Json Object consists of TextureKey-ResourceLocation pairs
	 * @param textureSupplier {@link rikka.librikka.model.loader.Mark}
	 * @param bakedModelSupplier A functional interface which returns an instance of a IBakedModel
	 */
	public ModelGeometryWrapper(
			@Nullable JsonObject textureJsonObj, 
			@Nullable Class<?> textureSupplier,
			Function<ModelGeometryBakeContext, IBakedModel> bakedModelSupplier) {
		this.bakedModelSupplier = bakedModelSupplier;
		
		this.textures = new HashMap<>();
		ResourceLocation atlasLoc = atlasLoc();
		
		if (textureJsonObj != null) {
			for (Entry<String, JsonElement> entry: textureJsonObj.entrySet()) {
				String key = entry.getKey();
				String textureLoc = entry.getValue().getAsString();
				ResourceLocation textureResLoc = new ResourceLocation(textureLoc);
				this.textures.put(key, new Material(atlasLoc, textureResLoc));
			}
		}
		
		if (textureSupplier != null) {
			EasyTextureLoader.foreachMarker(textureSupplier, scanEndClass(), (cls, field)->{
				String textureName = EasyTextureLoader.getMarkerValue(field);
				if (!textureName.startsWith("#")) {
					String key = "resloc#" + textureName.toString();
					ResourceLocation textureResLoc = new ResourceLocation(textureName);
					this.textures.put(key, new Material(atlasLoc, textureResLoc));
				}
			});
		}
	}
	
	@SuppressWarnings("deprecation")
	public ResourceLocation atlasLoc() {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
	
	public Class<?> scanEndClass() {
		return CodeBasedModel.class;
	}
	
	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery,
			Function<Material, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
			ItemOverrideList overrides, ResourceLocation modelLocation) {

		final ModelGeometryBakeContext context = new ModelGeometryBakeContext(
				owner, bakery, spriteGetter, modelTransform, overrides, modelLocation,
				this.textures);

		IBakedModel model = bakedModelSupplier.apply(context);
		
		if (model instanceof CodeBasedModel) {
	    	EasyTextureLoader.applyTextures(model, model.getClass(), scanEndClass(), (textureName)->{
	    		if (textureName.startsWith("#")) {
	    			return context.getTextureByKey(textureName.substring(1));
	    		} else {
	    			return context.getTexture(new ResourceLocation(textureName));
	    		}
	    	});
			
			model = ((CodeBasedModel) model).bake(context);
		}

		return model;
	}

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner,
			Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return this.textures.values();
	}
}
