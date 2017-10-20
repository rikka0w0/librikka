package rikka.librikka.model.quadbuilder;

import java.awt.Color;

import com.google.common.primitives.Ints;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedQuadHelper {
    private static int[] lightValueMap = new int[] {0x8100, 0x7F00, 0x810000, 0x7F0000, 0x81, 0x7F};
    
    public static int calculateLightValue(EnumFacing facing) {
    	return facing==null ? 0 : 0;
    }
	
    public static BakedQuad bake(
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3,
            float x4, float y4, float z4, float u4, float v4,
            TextureAtlasSprite texture) {

    	int normal = calculatePackedNormal(
    					x1, y1, z1,
    					x2, y2, z2,
    					x3, y3, z3,
    					x4, y4, z4);
    	
    	EnumFacing side = getFacingFromVertexes(
				x1, y1, z1,
				x2, y2, z2,
				x3, y3, z3,
				x4, y4, z4);
    	
    	BakedQuad quad = new BakedQuad(Ints.concat(
                BakedQuadHelper.vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, u1, v1, normal),
                BakedQuadHelper.vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, u2, v2, normal),
                BakedQuadHelper.vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, u3, v3, normal),
                BakedQuadHelper.vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), texture, u4, v4, normal)
        ), 0, side, texture, true, DefaultVertexFormats.ITEM);

    	return quad;
    }
    
    /**
     * Create an vertex, in DefaultVertexFormats.ITEM, 
     * @param x
     * @param y
     * @param z
     * @param color AARRGGBB, if you are not sure just put {@link java.awt.Color.WHITE.getRGB()}
     * @param texture
     * @param u 0-16 exclusive
     * @param v 0-16 exclusive
     * @param normal the packed representation of the normal vector, see calculatePackedNormal()
     * @return an int array which describes the vertex
     */
    public static int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v, int normal) {
        return new int[]{
                Float.floatToRawIntBits(x),
                Float.floatToRawIntBits(y),
                Float.floatToRawIntBits(z),
                color,
                Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                normal
        };
    }
    
    /**
     * Calculate the normal vector based on four input coordinates
     * @return the packed normal, ZZYYXX
     */
    public static int calculatePackedNormal(
    		float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4) {
    	
    	float xp = x4-x2;
    	float yp = y4-y2;
    	float zp = z4-z2;
    	
    	float xq = x3-x1;
    	float yq = y3-y1;
    	float zq = z3-z1;
    	
    	//Cross Product
        float xn = yq*zp - zq*yp;
        float yn = zq*xp - xq*zp;
        float zn = xq*yp - yq*xp;

        //Normalize
        float norm = (float) (1.0 / Math.sqrt(xn*xn + yn*yn + zn*zn));
        xn *= norm;
        yn *= norm;
        zn *= norm;

        int x = ((byte)(xn * 127)) & 0xFF;
        int y = ((byte)(yn * 127)) & 0xFF;
        int z = ((byte)(zn * 127)) & 0xFF;
        return x | (y << 0x08) | (z << 0x10);
    }
    
    /**
     * Determine the facing based on four input coordinates
     * @return facing
     */
    public static EnumFacing getFacingFromVertexes(
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4) {
    	float xp = x4-x2;
    	float yp = y4-y2;
    	float zp = z4-z2;
    	
    	float xq = x3-x1;
    	float yq = y3-y1;
    	float zq = z3-z1;
    	
    	//Cross Product
        float xn = yq*zp - zq*yp;
        float yn = zq*xp - xq*zp;
        float zn = xq*yp - yq*xp;

        //{xn, yn, zn} = normal vector
        return LightUtil.toSide(xn, yn, zn);
    }
}
