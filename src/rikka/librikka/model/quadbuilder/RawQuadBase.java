package rikka.librikka.model.quadbuilder;

import net.minecraft.util.math.MathHelper;
import rikka.librikka.math.MathAssitant;

public abstract class RawQuadBase<T extends RawQuadBase> implements IRawElement<RawQuadBase>{
	protected final float[][] vertexes;
	
	protected RawQuadBase(float maxX, float maxY, float maxZ) {
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
	}
	
	protected RawQuadBase(float[][] vertexes) {
        this.vertexes = new float[vertexes.length][];

        for (int i = 0; i < vertexes.length; i++) {
            this.vertexes[i] = new float[vertexes[i].length];
            for (int j = 0; j < vertexes[i].length; j++)
                this.vertexes[i][j] = vertexes[i][j];
        }
	}
	
    @Override
    public RawQuadBase translateCoord(float x, float y, float z) {
        for (int i = 0; i < this.vertexes.length; i++) {
            this.vertexes[i][0] += x;
            this.vertexes[i][1] += y;
            this.vertexes[i][2] += z;
        }

        return this;
    }
    
    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of X Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
    @Override
    public RawQuadBase rotateAroundX(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);

        for (int i = 0; i < this.vertexes.length; i++) {						//	X	Y	Z
            float x = this.vertexes[i][0];										//	1	0	0
            float y = this.vertexes[i][1] * cos - this.vertexes[i][2] * sin;	//	0	cos	-sin
            float z = this.vertexes[i][1] * sin + this.vertexes[i][2] * cos;	//	0	sin	cos
            this.vertexes[i][0] = x;
            this.vertexes[i][1] = y;
            this.vertexes[i][2] = z;
        }

        return this;
    }

    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of Y Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
    @Override
    public RawQuadBase rotateAroundY(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);

        for (int i = 0; i < this.vertexes.length; i++) {						//	X		Y	Z
            float x = 	this.vertexes[i][0] * cos + this.vertexes[i][2] * sin;	//	cos		0	sin
            float y = 	this.vertexes[i][1];									//	0		1	0
            float z = - this.vertexes[i][0] * sin + this.vertexes[i][2] * cos;	//	-sin	0	cos
            this.vertexes[i][0] = x;
            this.vertexes[i][1] = y;
            this.vertexes[i][2] = z;
        }

        return this;
    }
    
    /**
     * 	.^x<br>
     * . out of the screen<br>
     * x into the screen<br>
     * ^ point towards the positive direction of Z Axis<br>
     * Right-Hand-Rule applies<br>
     * Check out https://open.gl/transformations for details
     * @param angle Rotate the part by a given angle
     */
    @Override
    public RawQuadBase rotateAroundZ(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);

        for (int i = 0; i < this.vertexes.length; i++) {						//	X		Y	Z
            float x = this.vertexes[i][0] * cos - this.vertexes[i][1] * sin;	//	cos	-sin	0
            float y = this.vertexes[i][0] * sin + this.vertexes[i][1] * cos;	//	sin	cos		0
            float z = this.vertexes[i][2];										//	0		0	1
            this.vertexes[i][0] = x;
            this.vertexes[i][1] = y;
            this.vertexes[i][2] = z;
        }

        return this;
    }

    @Override
    public RawQuadBase rotateAroundVector(float angle, float x, float y, float z) {
        //Normalize the axis vector
        float length = MathHelper.sqrt(x * x + y * y + z * z);
        if (length < 1e-12f)
        	return this;	//length is 0, [x, y, z] does not represent a vector with valid direction
        
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
	public RawQuadBase scale(float scale) {
		for (int i = 0; i < this.vertexes.length; i++) {
            this.vertexes[i][0] *= scale;
            this.vertexes[i][1] *= scale;
            this.vertexes[i][2] *= scale;
		}
		return this;
	}

	@Override
	public abstract T clone();
}
