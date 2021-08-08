package rikka.librikka.model.quadbuilder;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import rikka.librikka.math.ByteStream;
import rikka.librikka.math.MathAssitant;

/** Modified from BuildCraft source code */
@OnlyIn(Dist.CLIENT)
public class MutableVertex implements ITransformable<MutableVertex> {
    /** The position of this vertex. */
    public float position_x, position_y, position_z;
    /** The normal of this vertex. Might not be normalised. Default value is [0, 1, 0]. */
    public float normal_x, normal_y, normal_z;
    /** The colour of this vertex, where each one is a number in the range 0-255. Default value is 255. */
    public short colour_r, colour_g, colour_b, colour_a;
    /** The texture co-ord of this vertex. Should usually be between 0-1 */
    public float tex_u, tex_v;
    /** The light of this vertex. Should be in the range 0-15. */
    public byte light_block, light_sky;
    
    public final VertexFormat format;

    public MutableVertex(VertexFormat format) {
    	this.format = format;
    	
        normal_x = 0;
        normal_y = 1;
        normal_z = 0;

        colour_r = 0xFF;
        colour_g = 0xFF;
        colour_b = 0xFF;
        colour_a = 0xFF;
    }
    
    public MutableVertex(int[] data, int offset, VertexFormat format) {
    	this.format = format;

    	ByteStream stream = new ByteStream(data, format.getVertexSize()*4);
    	stream.seek(offset*4);
    	boolean unsupported = false;
    	for (VertexFormatElement element: format.getElements()) {
    		switch(element.getUsage()) {
			case POSITION:
				if (element.getType() == VertexFormatElement.Type.FLOAT &&
					element.getElementCount() == 3) {
					// POSITION_3F
					position_x = stream.getFloat();
					position_y = stream.getFloat();
					position_z = stream.getFloat();
				} else {
					unsupported = true;
				}
				break;
			case COLOR:
				if (element.getType() == VertexFormatElement.Type.UBYTE &&
				element.getElementCount() == 4) {
			        // COLOR_4UB
			        colouri(stream.getInt());
				} else {
					unsupported = true;
				}
				break;
			case UV:
				if (element.getType() == VertexFormatElement.Type.FLOAT &&
				element.getElementCount() == 2) {
			        // TEX_2F
			        tex_u = stream.getFloat();
			        tex_v = stream.getFloat();
				} else {
//					unsupported = true;
					break;
				}
				break;
			case NORMAL:
				if (element.getType() == VertexFormatElement.Type.BYTE &&
				element.getElementCount() == 3) {
			        // NORMAL_3B
			        normal_z = stream.getByte();
			        normal_y = stream.getByte();
			        normal_x = stream.getByte();
				} else {
					unsupported = true;
				}
				break;
			default:
				stream.skip(element.getByteSize());
				break;
    		};
    	}

        lightf(1,1);
        
        if (unsupported)
        	throw new RuntimeException("Vertex Format is not supported: " + format.toString());
    }
    
    @Override
    public MutableVertex clone() {
    	MutableVertex ret = new MutableVertex(this.format);
    	ret.position_x = position_x;
    	ret.position_y = position_y;
    	ret.position_z = position_z;
    	
    	ret.normal_x = normal_x;
    	ret.normal_y = normal_y;
    	ret.normal_z = normal_z;

    	ret.colour_r = colour_r;
    	ret.colour_g = colour_g;
    	ret.colour_b = colour_b;
    	ret.colour_a = colour_a;
    	
    	ret.tex_u = tex_u;
    	ret.tex_v = tex_v;
    	ret.light_block = light_block;
    	ret.light_sky = light_sky;
    	return ret;
    }

    @Override
    public String toString() {
        return "{ pos = [ " + position_x + ", " + position_y + ", " + position_z //
                + " ], norm = [ " + normal_x + ", " + normal_y + ", " + normal_z//
                + " ], colour = [ " + colour_r + ", " + colour_g + ", " + colour_b + ", " + colour_a//
                + " ], tex = [ " + tex_u + ", " + tex_v //
                + " ], light_block = " + light_block + ", light_sky = " + light_sky + " }";
    }

    public void toBakedItem(int[] data, int offset) {
    	ByteStream stream = new ByteStream(data);
    	boolean unsupported = false;
    	
    	stream.seek(offset*4);
    	for (VertexFormatElement element: format.getElements()) {
    		switch(element.getUsage()) {
			case POSITION:
				if (element.getType() == VertexFormatElement.Type.FLOAT &&
					element.getElementCount() == 3) {
					// POSITION_3F
					stream.pushFloat(position_x);
					stream.pushFloat(position_y);
					stream.pushFloat(position_z);
				} else {
					unsupported = true;
				}
				break;
			case COLOR:
				if (element.getType() == VertexFormatElement.Type.UBYTE &&
				element.getElementCount() == 4) {
			        // COLOR_4UB
			        stream.pushInt(colourRGBA());
				} else {
					unsupported = true;
				}
				break;
			case UV:
				if (element.getType() == VertexFormatElement.Type.FLOAT &&
				element.getElementCount() == 2) {
			        // TEX_2F
					stream.pushFloat(tex_u);
					stream.pushFloat(tex_v);
				} else if (element.getType() == VertexFormatElement.Type.BYTE &&
						element.getElementCount() == 2) {
					// TEX_2SB
					stream.pushShort((short) 1);
				} else {
					break;
				}
				break;
			case NORMAL:
				if (element.getType() == VertexFormatElement.Type.BYTE &&
				element.getElementCount() == 3) {
			        // NORMAL_3B
					stream.pushByte((byte) normal_z);
					stream.pushByte((byte) normal_y);
					stream.pushByte((byte) normal_x);
				} else {
					unsupported = true;
				}
				break;
			default:
				stream.skip(element.getByteSize());
				break;
    		};
    	}
    	
        if (unsupported)
        	throw new RuntimeException("Vertex Format is not supported: " + format.toString());
    }

    // Mutating
    public MutableVertex positiond(double x, double y, double z) {
        return positionf((float) x, (float) y, (float) z);
    }

    public MutableVertex positionf(float x, float y, float z) {
        position_x = x;
        position_y = y;
        position_z = z;
        return this;
    }

    public MutableVertex normali(int combined) {
        normal_x = ((combined >> 0) & 0xFF) / 0x7f;
        normal_y = ((combined >> 8) & 0xFF) / 0x7f;
        normal_z = ((combined >> 16) & 0xFF) / 0x7f;
        return this;
    }

    public int normalToPackedInt() {
        return normalAsByte(normal_x, 0) //
                | normalAsByte(normal_y, 8) //
                | normalAsByte(normal_z, 16);
    }

    private static int normalAsByte(float norm, int offset) {
        int as = (int) (norm * 0x7f);
        return as << offset;
    }

    public MutableVertex colouri(int rgba) {
        return colouri(rgba, rgba >> 8, rgba >> 16, rgba >>> 24);
    }

    public MutableVertex colouri(int r, int g, int b, int a) {
        colour_r = (short) (r & 0xFF);
        colour_g = (short) (g & 0xFF);
        colour_b = (short) (b & 0xFF);
        colour_a = (short) (a & 0xFF);
        return this;
    }

    public int colourRGBA() {
        int rgba = 0;
        rgba |= (colour_r & 0xFF) << 0;
        rgba |= (colour_g & 0xFF) << 8;
        rgba |= (colour_b & 0xFF) << 16;
        rgba |= (colour_a & 0xFF) << 24;
        return rgba;
    }

    public MutableVertex lightf(float block, float sky) {
        return lighti((int) (block * 0xF), (int) (sky * 0xF));
    }

    public MutableVertex lighti(int block, int sky) {
        light_block = (byte) block;
        light_sky = (byte) sky;
        return this;
    }

    public MutableVertex texf(float u, float v) {
        tex_u = u;
        tex_v = v;
        return this;
    }
    
    public float get(Direction.Axis axis) {
		if (axis == Direction.Axis.X) {
			return this.position_x;
		} else if (axis == Direction.Axis.Y) {
			return this.position_y;
		} else if (axis == Direction.Axis.Z) {
			return this.position_z;
		}
		return Float.NaN;
    }
    
    public void offset(Direction.Axis axis, float val) {
		if (axis == Direction.Axis.X) {
			this.position_x += val;
		} else if (axis == Direction.Axis.Y) {
			this.position_y += val;
		} else if (axis == Direction.Axis.Z) {
			this.position_z += val;
		}
    }

    ////////////////////////////
    ///
    ////////////////////////////
	@Override
	public MutableVertex translateCoord(float x, float y, float z) {
		position_x += x;
		position_y += y;
		position_z += z;
		return this;
	}

	@Override
	public MutableVertex rotateAroundX(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);

        float[] vertex = new float[] {position_x, position_y, position_z};
														// X	Y	Z
		float x = vertex[0]; 							// 1	0	0
		float y = vertex[1] * cos - vertex[2] * sin; 	// 0	cos	-sin
		float z = vertex[1] * sin + vertex[2] * cos; 	// 0	sin	cos
		position_x = x;
		position_y = y;
		position_z = z;

        return this;
	}

	@Override
	public MutableVertex rotateAroundY(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);

        float[] vertex = new float[] {position_x, position_y, position_z};
														// X	Y	Z
		float x = vertex[0] * cos + vertex[2] * sin; 	// cos	0	sin
		float y = vertex[1]; 							// 0	1	0
		float z = -vertex[0] * sin + vertex[2] * cos; 	// -sin	0	cos
		position_x = x;
		position_y = y;
		position_z = z;

        return this;
	}

	@Override
	public MutableVertex rotateAroundZ(float angle) {
        float cos = MathAssitant.cosAngle(angle);
        float sin = MathAssitant.sinAngle(angle);

        float[] vertex = new float[] {position_x, position_y, position_z};
														// X	Y		Z
		float x = vertex[0] * cos - vertex[1] * sin;	// cos	-sin	0
		float y = vertex[0] * sin + vertex[1] * cos;	// sin	cos		0
		float z = vertex[2];							// 0	0		1
		position_x = x;
		position_y = y;
		position_z = z;

        return this;
	}

    @Override
    public MutableVertex rotateAroundVector(float angle, float x, float y, float z) {
        //Normalize the axis vector
        float length = Mth.sqrt(x * x + y * y + z * z);
        if (length < 1e-12f)
        	return this;	//length is 0, [x, y, z] does not represent a vector with valid direction
        
        x = x / length;
        y = y / length;
        z = z / length;

        angle = angle * 0.01745329252F;    //Cast to radian
        float cos = Mth.cos(angle);
        float sin = Mth.sin(angle);

        float[] vertex = new float[] {position_x, position_y, position_z};
        float d0 = vertex[0] * (cos + x * x * (1 - cos)) + vertex[1] * (x * y * (1 - cos) - z * sin) + vertex[2] * (x * z * (1 - cos) + y * sin);
        float d1 = vertex[0] * (x * y * (1 - cos) + z * sin) + vertex[1] * (cos + y * y * (1 - cos)) + vertex[2] * (y * z * (1 - cos) - x * sin);
        float d2 = vertex[0] * (x * z * (1 - cos) - y * sin) + vertex[1] * (y * z * (1 - cos) + x * sin) + vertex[2] * (cos + z * z * (1 - cos));
        position_x = d0;
        position_y = d1;
        position_z = d2;

        return this;
    }

	@Override
	public MutableVertex scale(float scale) {
		position_x *= scale;
		position_y *= scale;
		position_z *= scale;
		return this;
	}
}
