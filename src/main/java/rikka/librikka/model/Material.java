package rikka.librikka.model;

import net.minecraft.util.ResourceLocation;

/**
 * For 1.15.2 compatibility
 */
public class Material {
	public final ResourceLocation atlas;
	public final ResourceLocation texture;
	
	public Material(ResourceLocation atlas, ResourceLocation texture) {
		this.atlas = atlas;
		this.texture = texture;
	}
	
	public ResourceLocation getTextureLoc() {
		return this.texture;
	}
}
