package rikka.librikka.model.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.base.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class EasyTextureLoader {
	public static void registerTextures(@Nonnull Object target, @Nonnull Set<ResourceLocation> list) {
        for (Field field: target.getClass().getDeclaredFields()) {
        	if (field.getType().isAssignableFrom(TextureAtlasSprite.class) && field.isAnnotationPresent(EasyTextureLoader.Mark.class)) {
        		EasyTextureLoader.Mark texture = field.getAnnotation(EasyTextureLoader.Mark.class);
        		String textureLoc = texture.value();
        		list.add(new ResourceLocation(textureLoc));
        	}
        }
	}
	
	public static void applyTextures(@Nonnull Object target, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        for (Field field: target.getClass().getDeclaredFields()) {
        	if (field.getType().isAssignableFrom(TextureAtlasSprite.class) && field.isAnnotationPresent(EasyTextureLoader.Mark.class)) {
        		EasyTextureLoader.Mark texture = field.getAnnotation(EasyTextureLoader.Mark.class);
        		String textureLoc = texture.value();
        		TextureAtlasSprite sprite = bakedTextureGetter.apply(new ResourceLocation(textureLoc));
        		
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
					System.err.println("An error occured while populating field " + field.getName() + "in class " + target.getClass().toString());
					e.printStackTrace();
				}
        	}
        }
	}
	
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Mark {
    	String value();
    }
}
