package rikka.librikka.model.quadbuilder;

import java.util.List;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Rikka0w0
 * @param <T> the return type of functions
 * @param <E> the element type
 */
@SuppressWarnings("unchecked")
@OnlyIn(Dist.CLIENT)
public interface IRawGroupWrapper<T extends IRawGroupWrapper<?,?>, E extends ITransformable<?>> extends ITransformable<T> {
	/**
	 * This should not be called directly!
	 * @return all elements
	 */
	List<E> getElements();

    @Override
    default T translateCoord(float x, float y, float z) {
        for (E part : getElements())
            part.translateCoord(x, y, z);

        return (T) this;
    }

    @Override
    default T rotateAroundX(float angle) {
        for (E part : getElements())
            part.rotateAroundX(angle);

        return (T) this;
    }

    @Override
    default T rotateAroundY(float angle) {
        for (E part : getElements())
            part.rotateAroundY(angle);

        return (T) this;
    }

    @Override
    default T rotateAroundZ(float angle) {
        for (E part : getElements())
            part.rotateAroundZ(angle);

        return (T) this;
    }

    @Override
    default T rotateToVec(float xStart, float yStart, float zStart,
                                    float xEnd, float yEnd, float zEnd) {
        for (E part : getElements())
            part.rotateToVec(xStart, yStart, zStart, xEnd, yEnd, zEnd);

        return (T) this;
    }

    @Override
    default T rotateToDirection(Direction direction) {
        for (E part : getElements())
            part.rotateToDirection(direction);

        return (T) this;
    }

    @Override
    default T rotateAroundVector(float angle, float x, float y, float z) {
        for (E part : getElements())
            part.rotateAroundVector(angle, x, y, z);

        return (T) this;
    }
    
    @Override
    default T scale(float scale) {
        for (E part : getElements())
			part.scale(scale);
		return (T) this;
	}
}
