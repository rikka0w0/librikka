package rikka.librikka.model.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

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
	@Deprecated
	public static void registerTextures(@Nonnull Object target, @Nonnull Set<ResourceLocation> list) {
		registerTextures(target, Object.class, list);
	}
	
	public static void registerTextures(@Nonnull Object target, @Nonnull Class<?> toSuperClass, @Nonnull Set<ResourceLocation> list) {
		for (Class<?> cls = target.getClass(); cls != toSuperClass; cls = cls.getSuperclass()) {
	        for (Field field: cls.getDeclaredFields()) {
	        	if (field.getType().isAssignableFrom(TextureAtlasSprite.class) && field.isAnnotationPresent(EasyTextureLoader.Mark.class)) {
	        		EasyTextureLoader.Mark texture = field.getAnnotation(EasyTextureLoader.Mark.class);
	        		String textureLoc = texture.value();
	        		if (!textureLoc.startsWith("#"))
	        			list.add(new ResourceLocation(textureLoc));
	        	}
	        }
		}
	}

	@Deprecated
	public static void applyTextures(@Nonnull Object target, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		applyTextures(target, Object.class, bakedTextureGetter);
	}
	
	public static void applyTextures(@Nonnull Object target, @Nonnull Class<?> toSuperClass, 
			@Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		applyTextures(target, target.getClass(), toSuperClass, 
				((Function<String, ResourceLocation>)(ResourceLocation::new)).andThen(bakedTextureGetter));
	}

	/**
	 * 
	 * @param target
	 * @param fromClass
	 * @param toSuperClass
	 * @param spriteMapper
	 */
	public static void applyTextures(
			@Nonnull final Object target, 
			@Nonnull final Class<?> fromClass, 
			@Nonnull final Class<?> toSuperClass, 
			@Nonnull final Function<String, TextureAtlasSprite> spriteMapper) {

		foreachMarker(fromClass, toSuperClass, (cls, field)->{
			String textureLoc = getMarkerValue(field);
			TextureAtlasSprite sprite = spriteMapper.apply(textureLoc);

			boolean accessibilityChanged = false;
			if (!field.isAccessible()) {
				accessibilityChanged = true;
				field.setAccessible(true);
			}
			
			try {
				field.set(target, sprite);
				if (accessibilityChanged)
					field.setAccessible(true);
			} catch (Exception e) {
				System.err.println("An error occured while populating field " + field.getName() + "in class " + cls.toString());
				e.printStackTrace();
			}
		});
	}

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

	@Nullable
	public static String getMarkerValue(Field field) {
    	if (field.getType().isAssignableFrom(TextureAtlasSprite.class) && 
    			field.isAnnotationPresent(EasyTextureLoader.Mark.class)) {
    		EasyTextureLoader.Mark texture = field.getAnnotation(EasyTextureLoader.Mark.class);
    		return texture.value();
    	}
    	return null;
	}
	
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Mark {
    	String value();
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
