package rikka.librikka.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
	static void drawShape(PoseStack poseStack, VertexConsumer vertexConsumer, VoxelShape shapeIn, double x, double y,
			double z, float r, float g, float b, float a) {
		PoseStack.Pose pose = poseStack.last();
		shapeIn.forAllEdges((p_172987_, p_172988_, p_172989_, p_172990_, p_172991_, p_172992_) -> {
			float f = (float) (p_172990_ - p_172987_);
			float f1 = (float) (p_172991_ - p_172988_);
			float f2 = (float) (p_172992_ - p_172989_);
			float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
			f = f / f3;
			f1 = f1 / f3;
			f2 = f2 / f3;
			vertexConsumer
					.vertex(pose.pose(), (float) (p_172987_ + x), (float) (p_172988_ + y), (float) (p_172989_ + z))
					.color(r, g, b, a).normal(pose.normal(), f, f1, f2).endVertex();
			vertexConsumer
					.vertex(pose.pose(), (float) (p_172990_ + x), (float) (p_172991_ + y), (float) (p_172992_ + z))
					.color(r, g, b, a).normal(pose.normal(), f, f1, f2).endVertex();
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
			Camera activeRenderInfoIn = event.getCamera();
			CollisionContext context = CollisionContext.of(activeRenderInfoIn.getEntity());
			VoxelShape shape = ((ICustomBoundingBox) block).getBoundingShape(blockstate, world, blockpos,context);
			float[] color = ((ICustomBoundingBox) block).getBoundingColor(blockstate, world, blockpos,context);

			if (color.length < 4)
				return;

			Vec3 Vec3 = activeRenderInfoIn.getPosition();

			if (!blockstate.isAir() && world.getWorldBorder().isWithinBounds(blockpos)) {
				VertexConsumer ivertexbuilder2 = event.getMultiBufferSource().getBuffer(RenderType.lines());

				drawShape(event.getPoseStack(), ivertexbuilder2, shape,
						(double) blockpos.getX() - Vec3.x(),
						(double) blockpos.getY() - Vec3.y(),
						(double) blockpos.getZ() - Vec3.z(),
						color[0], color[1], color[2], color[3]);

			}

			event.setCanceled(true);
		}
	}
}
