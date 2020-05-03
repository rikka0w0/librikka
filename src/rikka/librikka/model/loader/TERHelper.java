package rikka.librikka.model.loader;

import java.util.function.Function;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import rikka.librikka.tileentity.TileEntityHelper;

@OnlyIn(Dist.CLIENT)
public class TERHelper {
    /**
     * Register a TileEntityRenderer for a TileEntityType, must be called during initialization
     */
    public static <T extends TileEntity> void bind(Class<T> teClass,
    		Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> rendererFactory) {
    	bind(ModLoadingContext.get().getActiveNamespace(), teClass, rendererFactory);
    }
    
    /**
     * Register a TileEntityRenderer for a TileEntityType, must be called during initialization
     */
    public static <T extends TileEntity> void bind(String namespace, Class<T> teClass,
    		Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> rendererFactory) {
    	TileEntityType<T> teType = TileEntityHelper.getTeType(namespace, teClass);
    	ClientRegistry.bindTileEntityRenderer(teType, rendererFactory);
    }
}
