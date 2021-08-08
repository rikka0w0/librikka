package rikka.librikka;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MarkedBlockHitResult<T> extends BlockHitResult {
	public final T subHit;

	public MarkedBlockHitResult(Vec3 p_82415_, Direction p_82416_, BlockPos p_82417_, boolean p_82418_, T subHit) {
		super(p_82415_, p_82416_, p_82417_, p_82418_);
		this.subHit = subHit;
	}

	public static <S> MarkedBlockHitResult<S> rayTrace(BlockPos pos, Vec3 start, Vec3 end, AABB boundingBox, S subHit) {
	      BlockHitResult raytraceresult = AABB.clip(ImmutableSet.of(boundingBox), start, end, pos);
	      return raytraceresult == null ?
	      		null :
	      		new MarkedBlockHitResult<S>(raytraceresult.getLocation(), raytraceresult.getDirection(), pos, false, subHit);
	}

	public static <S> MarkedBlockHitResult<S> iterate(@Nullable MarkedBlockHitResult<S> lastBest,
			BlockPos pos, Vec3 start, Vec3 end, AABB boundingBox, S subHit) {
    	MarkedBlockHitResult<S> next = MarkedBlockHitResult.rayTrace(pos, start, end, boundingBox, subHit);
        if (next == null)
            return lastBest;    //No intersection

        if (lastBest == null)
            return next;        //First intersection

        //The distance from start to the hit point
        double distLast = lastBest.getLocation().distanceToSqr(start);
        double distNext = next.getLocation().distanceToSqr(start);
        return distLast > distNext ? next : lastBest;    //Return the closer one
	}
}
