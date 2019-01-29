package rikka.librikka;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;

/**
 * Convert objects to bytes and vice versa
 */
public class ByteSerializer {
	public final static ByteSerializer instance = new ByteSerializer();

	public static final byte TYPE_BYTE = 0;
	public static final byte TYPE_INT = 1;
	public static final byte TYPE_FLOAT = 2;
	public static final byte TYPE_DOUBLE = 3;
	public static final byte TYPE_BOOLEAN = 4;
	public static final byte TYPE_STRING_UTF8 = 5;
	public static final byte TYPE_ENUMFACING = 6;

	public void packData(ByteBuf buf, Object obj) {
		Class cls = obj.getClass();
		if (cls == Byte.class) {
			buf.writeByte(TYPE_BYTE);
			buf.writeByte((Byte) obj);
		} else if (cls == Integer.class) {
			buf.writeByte(TYPE_INT);
			buf.writeInt((Integer) obj);
		} else if (cls == Float.class) {
			buf.writeByte(TYPE_FLOAT);
			buf.writeFloat((Float) obj);
		} else if (cls == Double.class) {
			buf.writeByte(TYPE_DOUBLE);
			buf.writeDouble((Double) obj);
		} else if (cls == Boolean.class) {
			buf.writeByte(TYPE_BOOLEAN);
			buf.writeBoolean((Boolean) obj);
		} else if (cls == String.class) {
			buf.writeByte(TYPE_STRING_UTF8);
			try {
				byte[] bytes = ((String) obj).getBytes("UTF8");
				buf.writeInt(bytes.length);
				buf.writeBytes(bytes);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}  else if (cls == EnumFacing.class) {
			buf.writeByte(TYPE_ENUMFACING);
			buf.writeByte(((EnumFacing) obj).ordinal());
		} else {
			throw new RuntimeException("Unsupported data type");
		}
	}

	public Object unpackData(ByteBuf buf) {
		return unpackData(buf, buf.readByte());
	}

	public Object unpackData(ByteBuf buf, byte typeID) {
		switch (typeID) {
		case TYPE_BYTE:
			return buf.readByte();
		case TYPE_INT:
			return buf.readInt();
		case TYPE_FLOAT:
			return buf.readFloat();
		case TYPE_DOUBLE:
			return buf.readDouble();
		case TYPE_BOOLEAN:
			return buf.readBoolean();
		case TYPE_STRING_UTF8:
			int length = buf.readInt();
			byte[] bytes = new byte[length];
			buf.readBytes(bytes,0,length);
			try {
				return new String(bytes, "UTF8");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		case TYPE_ENUMFACING:
			return EnumFacing.getFront(buf.readByte());
		default:
			throw new RuntimeException("Unsupported data type");
		}
	}
	
	public static boolean detectChange(Object a, Object b) {
		Class cls = a.getClass();

		if (cls == Byte.class) {
			return ((Byte)a).byteValue() != ((Byte)b).byteValue();
		} else if (cls == Integer.class) {
			return ((Integer)a).intValue() != ((Integer)b).intValue();
		} else if (cls == Float.class) {
			return ((Float)a).floatValue() != ((Float)b).floatValue();
		} else if (cls == Double.class) {
			return ((Double)a).doubleValue() != ((Double)b).doubleValue();		
		} else if (cls == Boolean.class) {
			return ((Boolean)a).booleanValue() != ((Boolean)b).booleanValue();
		} else if (cls == String.class) {
			return !a.equals(b);
		} else if (cls == EnumFacing.class) {
			return !a.equals(b);
		} else {
			return !a.equals(b);
		}
	}
}
