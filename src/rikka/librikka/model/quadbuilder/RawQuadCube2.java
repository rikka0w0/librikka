package rikka.librikka.model.quadbuilder;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RawQuadCube2 extends RawQuadBase<RawQuadCube2> {
	private final TextureAtlasSprite texture;
	private final float[] uv;
	
	/**
	 * Create a sign-like cube
	 * @param width
	 * @param height
	 * @param depth
	 * @param u
	 * @param v
	 * @param texture
	 * @param textureSize
	 * @param width2
	 * @param height2
	 * @param depth2
	 */
	public RawQuadCube2(float width, float height, float depth, int u, int v, TextureAtlasSprite texture, int textureSize, int width2, int height2, int depth2) {
		this(width, height, depth, texture, textureSize,
				depth2+width2+u			, 0+v,		depth2+width2+width2+u			, depth2+v,
				depth2+u				, 0+v,		depth2+width2+u					, depth2+v,
				depth2+u				, depth2+v,	depth2+width2+u					, depth2+height2+v,	
				depth2+depth2+width2+u	, depth2+v,	depth2+width2+depth2+width2+u	, depth2+height2+v,
				u+depth2+width2			, depth2+v,	depth2+depth2+width2+u			, depth2+height2+v,
				u						, depth2+v,	depth2+u						, depth2+height2+v		
				);
		
		translateCoord(0, 0.5F - height/2F, 0.5F - depth/2F);
	}
	
	public RawQuadCube2(float maxX, float maxY, float maxZ, TextureAtlasSprite texture, int textureSize, float... uv) {
		super(maxX, maxY, maxZ);
        this.texture = texture;
        
        this.uv = new float[uv.length];
        for (int i=0; i<uv.length; i++) {
        	this.uv[i] = uv[i] * 16F / (float)textureSize;
        }
	}
	
	private RawQuadCube2(float[][] vertexes, TextureAtlasSprite texture, float[] uv) {
		super(vertexes);
        this.texture = texture;
        this.uv = uv;
	}
    
	@Override
	public RawQuadCube2 clone() {
		return new RawQuadCube2(vertexes, texture, uv);
	}
	
	@Override
	public void bake(List<BakedQuad> list) {
		float uMin, uMax, vMin, vMax;

        //Down - Yneg
		if (uv[0] >= 0) {
	        uMin = uv[0];
	        uMax = uv[2];    //For 32x32 64x64 textures, this number is still 16 !!!!!
	        vMin = uv[1];
	        vMax = uv[3];
	        list.add(BakedQuadHelper.bake(
	               this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], uMin, vMin,	//uMin, vMax
	               this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], uMin, vMax,	//uMin, vMin
	               this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], uMax, vMax, 	//uMax, vMin
	               this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], uMax, vMin,	//uMax, vMax
	               texture));
		}
        
        //Up - Ypos
		if (uv[4] >= 0) {
	        uMin = uv[4];
	        uMax = uv[6];
	        vMin = uv[5];
	        vMax = uv[7];
	        list.add(BakedQuadHelper.bake(
	               this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], uMax, vMax,
	               this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], uMax, vMin,
	               this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], uMin, vMin,
	               this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], uMin, vMax,
	               texture));
		}


        //North - Zneg
		if (uv[8] >= 0) {
	        uMin = uv[8];
	        uMax = uv[10];
	        vMin = uv[9];
	        vMax = uv[11];
	        list.add(BakedQuadHelper.bake(
	               this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], uMax, vMin,
	               this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], uMin, vMin,
	               this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], uMin, vMax,
	               this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], uMax, vMax,
	               texture));
		}

        //South - Zpos
		if (uv[12] >= 0) {
	        uMin = uv[12];
	        uMax = uv[14];
	        vMin = uv[13];
	        vMax = uv[15];
	        list.add(BakedQuadHelper.bake(
	               this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], uMin, vMin,
	               this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], uMin, vMax,
	               this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], uMax, vMax,
	               this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], uMax, vMin,
	               texture));
		}
		
        //West - Xneg
		if (uv[16] >= 0) {
	        uMin = uv[16];
	        uMax = uv[18];
	        vMin = uv[17];
	        vMax = uv[19];
	        list.add(BakedQuadHelper.bake(
	               this.vertexes[3][0], this.vertexes[3][1], this.vertexes[3][2], uMax, vMin,
	               this.vertexes[2][0], this.vertexes[2][1], this.vertexes[2][2], uMin, vMin,
	               this.vertexes[6][0], this.vertexes[6][1], this.vertexes[6][2], uMin, vMax,
	               this.vertexes[7][0], this.vertexes[7][1], this.vertexes[7][2], uMax, vMax,
	               texture));
		}
		
        //East - Xpos
		if (uv[20] >= 0) {
	        uMin = uv[20];
	        uMax = uv[22];
	        vMin = uv[21];
	        vMax = uv[23];
	        list.add(BakedQuadHelper.bake(
	               this.vertexes[1][0], this.vertexes[1][1], this.vertexes[1][2], uMax, vMin,
	               this.vertexes[0][0], this.vertexes[0][1], this.vertexes[0][2], uMin, vMin,
	               this.vertexes[4][0], this.vertexes[4][1], this.vertexes[4][2], uMin, vMax,
	               this.vertexes[5][0], this.vertexes[5][1], this.vertexes[5][2], uMax, vMax,
	               texture));
		}
	}



}
