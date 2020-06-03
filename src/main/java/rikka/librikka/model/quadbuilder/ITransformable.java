package rikka.librikka.model.quadbuilder;

import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.math.MathAssitant;
import rikka.librikka.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public interface ITransformable<T extends ITransformable> {
	T translateCoord(float x, float y, float z);
    
    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of X Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
	T rotateAroundX(float angle);
    
    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of Y Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
	T rotateAroundY(float angle);

    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of Z Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
	T rotateAroundZ(float angle);

    default T rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd) {
        float distance = MathAssitant.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        if (distance < 1e-12f)
            return (T) this;	//length is 0, [x, y, z] does not represent a vector with valid direction

        this.rotateAroundY((float) (Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI));
        float z = zEnd - zStart;
        float x = xStart - xEnd;
        
        if (MathHelper.abs(z)< 1e-12f && MathHelper.abs(x)< 1e-12f)
        	z = 1;	//Vertical
        
        this.rotateAroundVector((float) (Math.acos((yEnd - yStart) / distance) * 180 / Math.PI), z, 0, x);

        return (T) this;
    }

    default T rotateToDirection(Direction direction) {
        switch (direction) {
            case DOWN:
                this.rotateAroundX(180);
                break;
            case NORTH:
                this.rotateAroundY(180);
                this.rotateAroundX(270);
                break;
            case SOUTH:
                this.rotateAroundX(90);
                break;
            case WEST:
                this.rotateAroundY(270);
                this.rotateAroundZ(90);
                break;
            case EAST:
                this.rotateAroundY(90);
                this.rotateAroundZ(270);
                break;
            default:
                break;
        }

        return (T) this;
    }

    T rotateAroundVector(float angle, float x, float y, float z);

    T scale(float scale);
    
    default T translateCoord(Vec3f offset) {
    	return this.translateCoord(offset.x, offset.y, offset.z);
    }
    
    default T rotateToVec(Vec3f vec1, Vec3f vec2) {
    	return this.rotateToVec(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
    }
    
    default T rotateAroundVector(float angle, Vec3f vec) {
    	return this.rotateAroundVector(angle, vec.x, vec.y, vec.z);
    }
}
