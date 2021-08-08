package rikka.librikka.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawSelectionEvent;

public interface ICustomBoundingBox {
	VoxelShape getBoundingShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);

	/**
	 * @param RGBA
	 */
	default float[] getBoundingColor(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return COLOR_DEFAULT;
	}

	public final static float[] COLOR_DEFAULT = new float[] {0.0F, 0.0F, 0.0F, 0.4F};

	@OnlyIn(Dist.CLIENT)
	static void drawShape(PoseStack matrixStackIn, VertexConsumer bufferIn, VoxelShape shapeIn, double xIn,
			double yIn, double zIn, float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStackIn.last().pose();
		shapeIn.forAllEdges((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
			bufferIn.vertex(matrix4f, (float) (p_230013_12_ + xIn), (float) (p_230013_14_ + yIn),
					(float) (p_230013_16_ + zIn)).color(red, green, blue, alpha).endVertex();
			bufferIn.vertex(matrix4f, (float) (p_230013_18_ + xIn), (float) (p_230013_20_ + yIn),
					(float) (p_230013_22_ + zIn)).color(red, green, blue, alpha).endVertex();
		});
	}

	@OnlyIn(Dist.CLIENT)
	public static void onBlockHighLight(DrawSelectionEvent event) {
		if (!(event.getTarget() instanceof BlockHitResult))
			return;

		Level world = Minecraft.getInstance().player.getCommandSenderWorld();
		BlockPos blockpos = ((BlockHitResult) event.getTarget()).getBlockPos();
		BlockState blockstate = world.getBlockState(blockpos);
		Block block = blockstate.getBlock();

		if (block instanceof ICustomBoundingBox) {
			Camera activeRenderInfoIn = event.getInfo();
			CollisionContext context = CollisionContext.of(activeRenderInfoIn.getEntity());
			VoxelShape shape = ((ICustomBoundingBox) block).getBoundingShape(blockstate, world, blockpos,context);
			float[] color = ((ICustomBoundingBox) block).getBoundingColor(blockstate, world, blockpos,context);

			if (color.length < 4)
				return;

			Vec3 Vec3 = activeRenderInfoIn.getPosition();

			if (!blockstate.isAir() && world.getWorldBorder().isWithinBounds(blockpos)) {
				VertexConsumer ivertexbuilder2 = event.getBuffers().getBuffer(RenderType.lines());
				drawShape(event.getMatrix(), ivertexbuilder2, shape,
						(double) blockpos.getX() - Vec3.x(),
						(double) blockpos.getY() - Vec3.y(),
						(double) blockpos.getZ() - Vec3.z(),
						color[0], color[1], color[2], color[3]);
			}

			event.setCanceled(true);
		}
	}
}
