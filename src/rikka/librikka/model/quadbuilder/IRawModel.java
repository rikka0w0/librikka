package rikka.librikka.model.quadbuilder;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface IRawModel<T extends IRawModel> {
    T clone();

    IRawModel translateCoord(float x, float y, float z);
    
    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of X Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
    IRawModel rotateAroundX(float angle);
    
    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of Y Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
    IRawModel rotateAroundY(float angle);

    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of Z Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
    IRawModel rotateAroundZ(float angle);

    IRawModel rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd);

    IRawModel rotateToDirection(EnumFacing direction);

    IRawModel rotateAroundVector(float angle, float x, float y, float z);

    IRawModel scale(float scale);
    
    /**
     * Convert vertex/texture data represented by this class to BakedQuads,
     * which can immediately be rendered by MineCraft
     *
     * @param list BakedQuads will be added to this list, must NOT be null!!!
     */
    void bake(List<BakedQuad> list);
}
