package rikka.librikka.model;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.IModelData;
import rikka.librikka.mod.LibRikka;
import rikka.librikka.model.loader.ModelGeometryWrapper;

public class GeneratedModelLoader implements IModelLoader<ModelGeometryWrapper> {
	public final static ResourceLocation id = new ResourceLocation(LibRikka.MODID, "generated");
	public final static GeneratedModelLoader instance = new GeneratedModelLoader();
	public final static List<BakedQuad> emptyQuads = ImmutableList.of();
	
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {

	}

	@Override
	public ModelGeometryWrapper read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		String type = GsonHelper.getAsString(modelContents, "type");
		JsonObject textures = GsonHelper.getAsJsonObject(modelContents, "textures");

		if (type.equals("placeholder")) {
			return new ModelGeometryWrapper(textures, null, (context)->{
				final TextureAtlasSprite particle = context.getTextureByKey("particle");
				return new CodeBasedModel() {
					@Override
					public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
						return emptyQuads;
					}

					@Override
					public TextureAtlasSprite getParticleIcon() {
						return particle;
					}

					@Override
					protected void bake(Function<ResourceLocation, TextureAtlasSprite> textureRegistry) {
				
					}
				};
			});
		}


		throw new RuntimeException("\"" + type + "\" is not implemented by " + id.toString());
	}

	////////////////////////////////
	/// DataGenerator helpers
	////////////////////////////////
	public static JsonObject commentDoNotModify(JsonObject jo) {
		jo.addProperty("__comment", "Generated Model, do not modify!");
		return jo;
	}
	
	public static JsonObject placeholder() {
		return placeholder(new ResourceLocation("minecraft:block/iron_block"));
	}
	
	public static JsonObject placeholder(ResourceLocation particle) {
		JsonObject root = new JsonObject();
		
		root.addProperty("loader", id.toString());
		root.addProperty("type", "placeholder");

		JsonObject textures = new JsonObject();
		textures.addProperty("particle", particle.toString());
		root.add("textures", textures);

		return root;
	}
}
