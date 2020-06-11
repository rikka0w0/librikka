package rikka.librikka.math;

public class ByteStream {
	private final int[] data;
	public final int size;	// In bytes
	private int position;	// In bytes

	public ByteStream(int[] data) {
		this(data, data.length * 4);
	}

	public ByteStream(int[] data, int size) {
		this.data = data;
		this.size = size;
		this.position = 0;
	}
	
	public int skip(int length) {
		return seek(this.position + length);
	}
	
	public int seek(int pos) {
		int old = this.position;
		if (pos > size)
			pos = size;
		this.position = pos;
		return old;
	}
	
	public boolean eof() {
		return position >= size;
	}

	public byte getByte() {
		if (eof())
			return 0;
		
		int pos = position / 4;
		int nibble = position % 4;

		int curInt = data[pos];
		int ret = curInt >> ((3-nibble)*8);

		position++;

		return (byte) (ret & 0xFF);
	}

	public void pushByte(byte theByte) {
		if (eof())
			return;
		
		int theInt = theByte;
		theInt &= 0xFF;

		int pos = position / 4;
		int nibble = position % 4;

		int curInt = data[pos];
		curInt &=~ (0xFF << ((3-nibble)*8));
		curInt |= theInt << ((3-nibble)*8);
		data[pos] = curInt;

		position++;
	}

	public short getShort() {
		int b1 = getByte();
		int b2 = getByte();
		
		b1 <<= 8;
		b2 <<= 0;
		
		b1 &= 0xFF00;
		b2 &= 0x00FF;
		
		return (short) (b1 | b2);
	}
	
	public void pushShort(short theShort) {
		int theInt = theShort;
		byte b2 = (byte) theInt;
		theInt >>= 8;
		byte b1 = (byte) theInt;
		
		pushByte(b1);
		pushByte(b2);
	}

	public int getInt() {
		int b1 = getByte();
		int b2 = getByte();
		int b3 = getByte();
		int b4 = getByte();
		
		b1 <<= 24;
		b2 <<= 16;
		b3 <<= 8;
		
		b1 &= 0xFF000000;
		b2 &= 0x00FF0000;
		b3 &= 0x0000FF00;
		b4 &= 0x000000FF;
		
		return b1 | b2 | b3 | b4;
	}
	
	public void pushInt(int theInt) {
		byte b4 = (byte) theInt;
		theInt >>= 8;
		byte b3 = (byte) theInt;
		theInt >>= 8;
		byte b2 = (byte) theInt;
		theInt >>= 8;
		byte b1 = (byte) theInt;
		
		pushByte(b1);
		pushByte(b2);
		pushByte(b3);
		pushByte(b4);
	}
	
	public float getFloat() {
		return Float.intBitsToFloat(getInt());
	}
	
	public void pushFloat(float theFloat) {
		pushInt(Float.floatToRawIntBits(theFloat));
	}
}