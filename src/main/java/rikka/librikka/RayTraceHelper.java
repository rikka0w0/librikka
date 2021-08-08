package rikka.librikka;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * BuildCraft magic
 *
 * @author Rikka0_0
 */
public class RayTraceHelper {
    /**
     * @param pos
     * @param start       absolute coordinate
     * @param end         absolute coordinate
     * @param boundingBox normal range: 0,0,0 - 1,1,1
     * @return
     */
    public static BlockHitResult rayTrace(BlockPos pos, Vec3 start, Vec3 end, AABB boundingBox) {
//        Vec3 Vec3 = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
//        Vec3 Vector3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        BlockHitResult raytraceresult = AABB.clip(ImmutableSet.of(boundingBox), start, end, pos);
        return raytraceresult == null ?
        		null :
        		new BlockHitResult(raytraceresult.getLocation(), raytraceresult.getDirection(), pos, false);
    }

    /**
     * A helper function for creating collision boxes, default facing: upwards, origin: block center
     * @param side
     * @param xStart
     * @param yStart
     * @param zStart
     * @param xEnd
     * @param yEnd
     * @param zEnd
     * @return
     */
    public static AABB createAABB (Direction side, float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd) {
        switch (side) {
		case DOWN:
			return new AABB(xStart, yStart, zStart, xEnd, yEnd, zEnd);
		case UP:
			return new AABB(xStart, 1-yEnd, zStart, xEnd, 1-yStart, zEnd);
		case NORTH:
			return new AABB(zStart, xStart, yStart, zEnd, xEnd, yEnd);
		case SOUTH:
			return new AABB(zStart, xStart, 1-yEnd, zEnd, xEnd, 1-yStart);
		case WEST:
			return new AABB(yStart, xStart, zStart, yEnd, xEnd, zEnd);
		case EAST:
			return new AABB(1-yEnd, xStart, zStart, 1-yStart, xEnd, zEnd);
        }
        return null;
    }
}
