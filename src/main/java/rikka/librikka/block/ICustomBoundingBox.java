package rikka.librikka.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;

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
	static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn,
			double yIn, double zIn, float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
		shapeIn.forEachEdge((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
			bufferIn.pos(matrix4f, (float) (p_230013_12_ + xIn), (float) (p_230013_14_ + yIn),
					(float) (p_230013_16_ + zIn)).color(red, green, blue, alpha).endVertex();
			bufferIn.pos(matrix4f, (float) (p_230013_18_ + xIn), (float) (p_230013_20_ + yIn),
					(float) (p_230013_22_ + zIn)).color(red, green, blue, alpha).endVertex();
		});
	}

	@OnlyIn(Dist.CLIENT)
	public static void onBlockHighLight(DrawHighlightEvent.HighlightBlock event) {
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
			
			Vector3d Vector3d = activeRenderInfoIn.getProjectedView();

			if (!blockstate.isAir(world, blockpos) && world.getWorldBorder().contains(blockpos)) {
				IVertexBuilder ivertexbuilder2 = event.getBuffers().getBuffer(RenderType.getLines());
				drawShape(event.getMatrix(), ivertexbuilder2, shape, 
						(double) blockpos.getX() - Vector3d.getX(),
						(double) blockpos.getY() - Vector3d.getY(), 
						(double) blockpos.getZ() - Vector3d.getZ(), 
						color[0], color[1], color[2], color[3]);
			}

			event.setCanceled(true);
		}
	}
}
