package rikka.librikka.model.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EasyTextureLoader {
	@Deprecated
	public static void registerTextures(@Nonnull Object target, @Nonnull Set<ResourceLocation> list) {
		registerTextures(target, Object.class, list);
	}
	
	public static void registerTextures(@Nonnull Object target, @Nonnull Class toSuperClass, @Nonnull Set<ResourceLocation> list) {
		for (Class cls = target.getClass(); cls != toSuperClass; cls = cls.getSuperclass()) {
	        for (Field field: cls.getDeclaredFields()) {
	        	if (field.getType().isAssignableFrom(TextureAtlasSprite.class) && field.isAnnotationPresent(EasyTextureLoader.Mark.class)) {
	        		EasyTextureLoader.Mark texture = field.getAnnotation(EasyTextureLoader.Mark.class);
	        		String textureLoc = texture.value();
	        		list.add(new ResourceLocation(textureLoc));
	        	}
	        }
		}
	}
	
	@Deprecated
	public static void applyTextures(@Nonnull Object target, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		applyTextures(target, Object.class, bakedTextureGetter);
	}
	
	public static void applyTextures(@Nonnull Object target, @Nonnull Class toSuperClass, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		for (Class cls = target.getClass(); cls != toSuperClass; cls = cls.getSuperclass()) {
			for (Field field: cls.getDeclaredFields()) {
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
						System.err.println("An error occured while populating field " + field.getName() + "in class " + cls.toString());
						e.printStackTrace();
					}
	        	}
	        }	
		}
	}
	
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Mark {
    	String value();
    }
    
    /**
     * @return a registered texture
     */
    public static TextureAtlasSprite getTexture(String textureName) {
    	return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureName);
    }
}
