package rikka.librikka.model.loader;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import rikka.librikka.blockentity.BlockEntityHelper;

@OnlyIn(Dist.CLIENT)
public class TERHelper {
    /**
     * Register a BlockEntityRenderer for a BlockEntityType, must be called during initialization
     */
    public static <T extends BlockEntity> void bind(Class<T> teClass,
    		BlockEntityRendererProvider<T> rendererFactory) {
    	bind(ModLoadingContext.get().getActiveNamespace(), teClass, rendererFactory);
    }

    /**
     * Register a BlockEntityRenderer for a BlockEntityType, must be called during initialization
     */
    public static <T extends BlockEntity> void bind(String namespace, Class<T> teClass,
    		BlockEntityRendererProvider<T> rendererFactory) {
    	BlockEntityType<T> teType = BlockEntityHelper.getBEType(namespace, teClass);
    	BlockEntityRenderers.register(teType, rendererFactory);
    }
}
