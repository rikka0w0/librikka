package rikka.librikka.block;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public interface ICustomBoundingBox {
	VoxelShape getBoundingShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context);
	
	/**
	 * @param RGBA
	 */
	default float[] getBoundingColor(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return COLOR_DEFAULT;
	}
	
	public final static float[] COLOR_DEFAULT = new float[] {0.0F, 0.0F, 0.0F, 0.4F};
	
	@OnlyIn(Dist.CLIENT)
	public static void drawSelectionBox(World world, ActiveRenderInfo p_215325_1_, BlockPos blockpos, VoxelShape shape) {
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager
				.lineWidth(Math.max(2.5F, (float) Minecraft.getInstance().mainWindow.getFramebufferWidth() / 1920.0F * 2.5F));
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.scalef(1.0F, 1.0F, 0.999F);
		double d0 = p_215325_1_.getProjectedView().x;
		double d1 = p_215325_1_.getProjectedView().y;
		double d2 = p_215325_1_.getProjectedView().z;
		WorldRenderer.drawShape(
				shape,
				(double) blockpos.getX() - d0, (double) blockpos.getY() - d1, (double) blockpos.getZ() - d2,
				0.0F, 0.0F, 0.0F, 0.4F);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();

	}

	@OnlyIn(Dist.CLIENT)
	public static void onBlockHighLight(DrawBlockHighlightEvent.HighlightBlock event) {
		World world = Minecraft.getInstance().player.getEntityWorld();
		BlockPos blockpos = event.getTarget().getPos();
		BlockState blockstate = world.getBlockState(blockpos);
		Block block = blockstate.getBlock();
		
		if (block instanceof ICustomBoundingBox) {
			ActiveRenderInfo activeRenderInfoIn = event.getInfo();
			ISelectionContext context = ISelectionContext.forEntity(activeRenderInfoIn.getRenderViewEntity());
			VoxelShape shape = ((ICustomBoundingBox) block).getBoundingShape(blockstate, world, blockpos,context);
			float[] color = ((ICustomBoundingBox) block).getBoundingColor(blockstate, world, blockpos,context);
			
			if (color.length < 4)
				return;

			drawSelectionBox(world, activeRenderInfoIn, blockpos, shape);

			event.setCanceled(true);
		}
	}
}
