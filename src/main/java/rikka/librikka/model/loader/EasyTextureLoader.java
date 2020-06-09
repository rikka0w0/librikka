package rikka.librikka.model.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;

@OnlyIn(Dist.CLIENT)
public class EasyTextureLoader {
	public static void foreachMarker(
			@Nonnull Class<?> fromClass, 
			@Nonnull Class<?> toSuperClass, 
			@Nonnull BiConsumer<Class<?>, Field> consumer) {
		for (Class<?> cls = fromClass; cls != toSuperClass; cls = cls.getSuperclass()) {
	        for (Field field: cls.getDeclaredFields()) {
	        	String textureLoc = getMarkerValue(field);
	        	if (textureLoc != null) {
	        		consumer.accept(cls, field);
	        	}
	        }
		}
	}

	/**
	 * Apply a TextureAtlasSprite to a field
	 * @param target
	 * @param field	the field can be final
	 * @param texture
	 */
	public static void applyTexture(Object target, Field field, TextureAtlasSprite texture) {
		boolean accessibilityChanged = false;
		if (!field.isAccessible()) {
			accessibilityChanged = true;
			field.setAccessible(true);
		}
		
		try {
			field.set(target, texture);
			if (accessibilityChanged)
				field.setAccessible(true);
		} catch (Exception e) {
			System.err.println("An error occured while applying texture " + field.getName() + " of " + target.getClass().getName());
			e.printStackTrace();
		}
	}

	/**
	 * @param field
	 * @return Depends on the value of the annotation: <p>
	 * 1. # only: "#" followed by the field name<p>
	 * 2. Otherwise: the value itself<p>
	 * If the return value starts with #, it is a key. <p>
	 * Otherwise it represents a {@link net.minecraft.util.ResourceLocation} <p>
	 * null if the Mark annotation does not exist.
	 */
	@Nullable
	public static String getMarkerValue(Field field) {
		if (field.getType().isAssignableFrom(TextureAtlasSprite.class)
				&& field.isAnnotationPresent(EasyTextureLoader.Mark.class)) {
			EasyTextureLoader.Mark texture = field.getAnnotation(EasyTextureLoader.Mark.class);
			String textureName = texture.value();
			if (textureName.equals("#"))
				return "#" + field.getName();
			else
				return texture.value();
		}
		return null;
	}
	
	/**
	 * Mark {@link net.minecraft.client.renderer.texture.TextureAtlasSprite} fields with in a class.
	 * Rule:<p>
	 * 1. Starts with #: Use the string after # as the key to retrieve texture from somewhere<p>
	 * 2. # only: Use the field name as the key to retrieve texture from somewhere<p>
	 * 3. Otherwise: parse as {@link net.minecraft.util.ResourceLocation}, e.g. "domain:path"
	 * @author Rikka0w0
	 */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Mark {
    	String value() default "#";
    }
    
    @SuppressWarnings("deprecation")
	public static Function<ResourceLocation, TextureAtlasSprite> blockTextureGetter() {
    	return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    }
    
    @SuppressWarnings("deprecation")
	public static boolean isBlockAtlas(TextureStitchEvent event) {
    	return event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    }
}
