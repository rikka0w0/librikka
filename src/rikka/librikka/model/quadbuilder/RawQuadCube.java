package rikka.librikka.model.quadbuilder;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Represent a 'raw' cube
 *
 * @author Rikka0_0
 */
@SideOnly(Side.CLIENT)
public class RawQuadCube extends RawQuadBase<RawQuadCube> {
    protected final TextureAtlasSprite[] icons;
    
    public RawQuadCube(float maxX, float maxY, float maxZ, TextureAtlasSprite icon) {
        this(maxX, maxY, maxZ, new TextureAtlasSprite[]{icon, icon, icon, icon, icon, icon});
    }

    public RawQuadCube(float maxX, float maxY, float maxZ, TextureAtlasSprite[] icons) {
    	super(maxX, maxY, maxZ);
        this.icons = icons;
    }

    @Deprecated
    public RawQuadCube(double[][] vertexes, TextureAtlasSprite[] icons) {
    	super(double2float(vertexes));
        this.icons = icons;
    }
    
    @Deprecated
    private static float[][] double2float(double[][] vertexes) {
        float[][] ret = new float[vertexes.length][];

        for (int i = 0; i < vertexes.length; i++) {
        	ret[i] = new float[vertexes[i].length];
            for (int j = 0; j < vertexes[i].length; j++)
            	ret[i][j] = (float) vertexes[i][j];
        }
        
        return ret;
    }

    public RawQuadCube(float[][] vertexes, TextureAtlasSprite[] icons) {
    	super(vertexes);
        this.icons = icons;
    }

    @Override
    public RawQuadCube clone() {
        return new RawQuadCube(this.vertexes, this.icons);
    }

    @Override
    public void bake(List<BakedQuad> list) {
        float uMin, uMax, vMin, vMax;

        //Down - Yneg
        if (this.icons[0] != null) {
            uMin = 0;
            uMax = 16;    //For 32x32 64x64 textures, this number is still 16 !!!!!
            vMin = 0;
            vMax = 16;
            list.add(BakedQuadHelper.bake(
            		this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], uMin, vMin, //uMin, vMax
            		this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], uMin, vMax, //uMin, vMin
            		this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], uMax, vMax, //uMax, vMin
            		this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], uMax, vMin, //uMax, vMax
            		this.icons[0]));
        }

        //Up - Ypos
        if (this.icons[1] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(BakedQuadHelper.bake(
                    this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], uMax, vMax,
                    this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], uMax, vMin,
                    this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], uMin, vMin,
                    this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], uMin, vMax,
                    this.icons[1]));
        }

        //North - Zneg
        if (this.icons[2] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(BakedQuadHelper.bake(
                    this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], uMax, vMin,
                    this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], uMin, vMin,
                    this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], uMin, vMax,
                    this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], uMax, vMax,
                    this.icons[2]));
        }

        //South - Zpos
        if (this.icons[3] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(BakedQuadHelper.bake(
                    this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], uMin, vMin,
                    this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], uMin, vMax,
                    this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], uMax, vMax,
                    this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], uMax, vMin,
                    this.icons[3]));
        }

        //West - Xneg
        if (this.icons[4] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(BakedQuadHelper.bake(
                    this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], uMax, vMin,
                    this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], uMin, vMin,
                    this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], uMin, vMax,
                    this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], uMax, vMax, 
                    this.icons[4]));
        }

        //East - Xpos
        if (this.icons[5] != null) {
            uMin = 0;
            uMax = 16;
            vMin = 0;
            vMax = 16;
            list.add(BakedQuadHelper.bake(
                    this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], uMax, vMin,
                    this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], uMin, vMin,
                    this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], uMin, vMax,
                    this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], uMax, vMax,
            		this.icons[5]));
        }
    }
}
