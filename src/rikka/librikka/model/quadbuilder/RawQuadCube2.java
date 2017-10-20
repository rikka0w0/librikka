package rikka.librikka.model.quadbuilder;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.math.MathAssitant;

@SideOnly(Side.CLIENT)
public class RawQuadCube2 implements IRawElement<RawQuadCube2> {
	private final float[][] vertexes;
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
        this.texture = texture;
        this.vertexes = new float[8][];
        float x = maxX / 2.0F;
        float z = maxZ / 2.0F;

        //Top
        this.vertexes[0] = new float[]{x, maxY, z};
        this.vertexes[1] = new float[]{x, maxY, -z};
        this.vertexes[2] = new float[]{-x, maxY, -z};
        this.vertexes[3] = new float[]{-x, maxY, z};

        //Bottom
        this.vertexes[4] = new float[]{x, 0, z};
        this.vertexes[5] = new float[]{x, 0, -z};
        this.vertexes[6] = new float[]{-x, 0, -z};
        this.vertexes[7] = new float[]{-x, 0, z};
        
        this.uv = new float[uv.length];
        for (int i=0; i<uv.length; i++) {
        	this.uv[i] = uv[i] * 16F / (float)textureSize;
        }
	}
	
	private RawQuadCube2(float[][] vertexes, TextureAtlasSprite texture, float[] uv) {
        this.texture = texture;
        this.uv = uv;
        this.vertexes = new float[vertexes.length][];

        for (int i = 0; i < vertexes.length; i++) {
            this.vertexes[i] = new float[vertexes[i].length];
            for (int j = 0; j < vertexes[i].length; j++)
                this.vertexes[i][j] = vertexes[i][j];
        }
	}
	
	@Override
    public RawQuadCube2 translateCoord(float x, float y, float z) {
        for (int i = 0; i < this.vertexes.length; i++) {
            this.vertexes[i][0] += x;
            this.vertexes[i][1] += y;
            this.vertexes[i][2] += z;
        }

        return this;
    }

    @Override
    public RawQuadCube2 rotateAroundX(float angle) {
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);

        for (int i = 0; i < this.vertexes.length; i++) {
            float d0 = this.vertexes[i][0];
            float d1 = this.vertexes[i][1] * f1 + this.vertexes[i][2] * f2;
            float d2 = this.vertexes[i][2] * f1 - this.vertexes[i][1] * f2;
            this.vertexes[i][0] = d0;
            this.vertexes[i][1] = d1;
            this.vertexes[i][2] = d2;
        }

        return this;
    }

    @Override
    public RawQuadCube2 rotateAroundY(float angle) {
        float f1 = MathHelper.cos(angle * 0.01745329252F);
        float f2 = MathHelper.sin(angle * 0.01745329252F);

        for (int i = 0; i < this.vertexes.length; i++) {
            float d0 = this.vertexes[i][0] * f1 + this.vertexes[i][2] * f2;
            float d1 = this.vertexes[i][1];
            float d2 = this.vertexes[i][2] * f1 - this.vertexes[i][0] * f2;
            this.vertexes[i][0] = d0;
            this.vertexes[i][1] = d1;
            this.vertexes[i][2] = d2;
        }

        return this;
    }

    @Override
    public RawQuadCube2 rotateAroundZ(float angle) {
        float f1 = MathHelper.cos(-angle * 0.01745329252F);
        float f2 = MathHelper.sin(-angle * 0.01745329252F);


        for (int i = 0; i < this.vertexes.length; i++) {
            float d0 = this.vertexes[i][0] * f1 + this.vertexes[i][1] * f2;
            float d1 = this.vertexes[i][1] * f1 - this.vertexes[i][0] * f2;
            float d2 = this.vertexes[i][2];
            this.vertexes[i][0] = d0;
            this.vertexes[i][1] = d1;
            this.vertexes[i][2] = d2;
        }

        return this;
    }

    @Override
    public RawQuadCube2 rotateToVec(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd) {
        float distance = MathAssitant.distanceOf(xStart, yStart, zStart, xEnd, yEnd, zEnd);
        this.rotateAroundY((float) (Math.atan2(zStart - zEnd, xEnd - xStart) * 180 / Math.PI));
        this.rotateAroundVector((float) (Math.acos((yEnd - yStart) / distance) * 180 / Math.PI), (zEnd - zStart) / distance, 0, (xStart - xEnd) / distance);

        return this;
    }

    @Override
    public RawQuadCube2 rotateToDirection(EnumFacing direction) {
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

    @Override
    public RawQuadCube2 rotateAroundVector(float angle, float x, float y, float z) {
        //Normalize the axis vector
        float length = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / length;
        y = y / length;
        z = z / length;

        angle = angle * 0.01745329252F;    //Cast to radian
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);

        for (int i = 0; i < this.vertexes.length; i++) {
            float d0 = this.vertexes[i][0] * (cos + x * x * (1 - cos)) + this.vertexes[i][1] * (x * y * (1 - cos) - z * sin) + this.vertexes[i][2] * (x * z * (1 - cos) + y * sin);
            float d1 = this.vertexes[i][0] * (x * y * (1 - cos) + z * sin) + this.vertexes[i][1] * (cos + y * y * (1 - cos)) + this.vertexes[i][2] * (y * z * (1 - cos) - x * sin);
            float d2 = this.vertexes[i][0] * (x * z * (1 - cos) - y * sin) + this.vertexes[i][1] * (y * z * (1 - cos) + x * sin) + this.vertexes[i][2] * (cos + z * z * (1 - cos));
            this.vertexes[i][0] = d0;
            this.vertexes[i][1] = d1;
            this.vertexes[i][2] = d2;
        }

        return this;
    }

	@Override
	public IRawModel scale(float scale) {
		for (int i = 0; i < this.vertexes.length; i++) {
            this.vertexes[i][0] *= scale;
            this.vertexes[i][1] *= scale;
            this.vertexes[i][2] *= scale;
		}
		return this;
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
