package rikka.librikka.math;

import javax.annotation.concurrent.Immutable;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

@Immutable
public class Vec3f {
    /**
     * X coordinate of Vec3D
     */
    public final float x;
    /**
     * Y coordinate of Vec3D
     */
    public final float y;
    /**
     * Z coordinate of Vec3D
     */
    public final float z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f add(int x, int y, int z) {
        return new Vec3f(this.x + x, this.y + y, this.z + z);
    }
    
    public Vec3f add(float x, float y, float z) {
        return new Vec3f(this.x + x, this.y + y, this.z + z);
    }

    public Vec3f add(Vec3i in) {
    	return new Vec3f(this.x + in.getX(), this.y + in.getY(), this.z + in.getZ());
    }
    
    public Vec3f add(Vec3f in) {
    	return new Vec3f(this.x + in.x, this.y + in.y, this.z + in.z);
    }
    
    /////////////////////////
    /// Rotation
    /////////////////////////
    public Vec3f rotateAroundX(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);
    	
    	return new Vec3f(	this.x,										//	1	0	0
    								this.y * cos 	- this.z * sin,		//	0	cos	-sin
    								this.y * sin 	+ this.z * cos);	//	0	sin	cos
    }
    
    public Vec3f rotateAroundY(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);
    	
    	return new Vec3f(	this.x * cos 	+ this.z * sin,		//	cos		0	sin
    									this.y,					//	0		1	0
    						-this.x * sin 	+ this.z * cos);	//	-sin	0	cos
    }
    
    public Vec3f rotateAroundZ(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);
    	
    	return new Vec3f(	this.x * cos - this.y * sin,		//	cos		-sin	0
    						this.x * sin + this.y * cos,		//	sin		cos		0
    													this.z);//	0		0		1
    }
    /////////////////////////
    /// Length and Distance
    /////////////////////////
    public float length() {
    	return MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public float distanceTo(Vec3f vec) {
        float dx = this.x - vec.x;
        float dy = this.y - vec.y;
        float dz = this.z - vec.z;
        return MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public float distanceTo(float x, float y, float z) {
        float dx = this.x - x;
        float dy = this.y - y;
        float dz = this.z - z;
        return MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public float distanceXZ(float x, float z) {
		float dx = x - this.x;
		float dz = z - this.z;
		
		return MathHelper.sqrt(dx*dx + dz*dz);
    }
    
    public float distanceXZ(Vec3f to) {
		float dx = to.x - this.x;
		float dz = to.z - this.z;
		
		return MathHelper.sqrt(dx*dx + dz*dz);
    }
    
	public float calcAngleFromXInDegree (Vec3f to) {
		float dx = to.x - this.x;
		float dz = to.z - this.z;
		
		float l = MathHelper.sqrt(dx*dx + dz*dz);
		float theta = (float) Math.acos(dx/l) * 180F / MathAssitant.PI;
		return dz>0 ? -theta : theta;
	}
    
    /////////////////////
    /// Object
    /////////////////////
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Vec3f)) {
            return false;
        } else {
            Vec3f vec3f = (Vec3f) obj;
            return Float.compare(vec3f.x, x) == 0 && Float.compare(vec3f.y, y) == 0 && Float.compare(vec3f.z, z) == 0;
        }
    }

    public int hashCode() {
        long j = Float.floatToIntBits(x);
        int i = (int) (j ^ j >>> 16);
        j = Float.floatToIntBits(y);
        i = 15 * i + (int) (j ^ j >>> 16);
        j = Float.floatToIntBits(z);
        i = 15 * i + (int) (j ^ j >>> 16);
        return i;
    }

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
