package rikka.librikka.model.quadbuilder;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.math.MathAssitant;

@SideOnly(Side.CLIENT)
public interface IRawElement<T extends IRawElement> extends IRawModel<IRawElement> {
    @Override
    IRawElement clone();

    default IRawElement rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd) {
        float distance = MathAssitant.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        if (distance < 1e-12f)
            return this;	//length is 0, [x, y, z] does not represent a vector with valid direction

        this.rotateAroundY((float) (Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI));
        float z = zEnd - zStart;
        float x = xStart - xEnd;
        
        if (MathHelper.abs(z)< 1e-12f && MathHelper.abs(x)< 1e-12f)
        	z = 1;	//Vertical
        
        this.rotateAroundVector((float) (Math.acos((yEnd - yStart) / distance) * 180 / Math.PI), z, 0, x);

        return this;
    }

    default IRawElement rotateToDirection(EnumFacing direction) {
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

        return this;
    }
}
