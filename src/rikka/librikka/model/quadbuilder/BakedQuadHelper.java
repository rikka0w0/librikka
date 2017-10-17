package rikka.librikka.model.quadbuilder;

import java.awt.Color;

import com.google.common.primitives.Ints;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedQuadHelper {
    private static int[] lightValueMap = new int[] {0x8100, 0x7F00, 0x810000, 0x7F0000, 0x81, 0x7F};
    
    public static int calculateLightValue(EnumFacing facing) {
    	return facing==null ? 0 : lightValueMap[facing.ordinal()];
    }
	
    public static BakedQuad bake(
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3,
            float x4, float y4, float z4, float u4, float v4,
            TextureAtlasSprite texture, int tint, EnumFacing side) {

        return new BakedQuad(Ints.concat(
                BakedQuadHelper.vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), texture, u1, v1, calculateLightValue(side)),
                BakedQuadHelper.vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), texture, u2, v2, calculateLightValue(side)),
                BakedQuadHelper.vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), texture, u3, v3, calculateLightValue(side)),
                BakedQuadHelper.vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), texture, u4, v4, calculateLightValue(side))
        ), tint, side, texture, true, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
    }
    
    public static int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v, int lightValue) {
        return new int[]{
                Float.floatToRawIntBits(x),
                Float.floatToRawIntBits(y),
                Float.floatToRawIntBits(z),
                color,
                Float.floatToRawIntBits(texture.getInterpolatedU(u)),
                Float.floatToRawIntBits(texture.getInterpolatedV(v)),
                lightValue
        };
    }
    
    public static BakedQuad bake(
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3,
            float x4, float y4, float z4, float u4, float v4,
            TextureAtlasSprite texture, int tint) {
    	return bake(
    			x1, y1, z1, u1, v1,
    			x2, y2, z2, u2, v2,
    			x3, y3, z3, u3, v3,
    			x4, y4, z4, u4, v4,
    			texture, tint, getFacingFromVertexes(
    					x1, y1, z1,
    					x2, y2, z2,
    					x3, y3, z3,
    					x4, y4, z4));
    }

    public static EnumFacing getFacingFromVertexes(
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4) {
        float xa = x1 - x2;
        float ya = y1 - y2;
        float za = z1 - z2;
        float xb = x3 - x4;
        float yb = y3 - y4;
        float zb = z3 - y4;

        //CrossProduct
        float xc = ya * zb - za * yb;
        float yc = za * xb - xa * zb;
        float zc = xa * yb - ya * xb;

        //{xc, yc, zc} = normal vector
        return LightUtil.toSide(xc, yc, zc);
    }
}
