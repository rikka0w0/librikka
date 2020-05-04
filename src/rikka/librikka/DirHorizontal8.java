package rikka.librikka;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction8;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

/**
 * Same as vanilla Direction8, except implements IStringSerializable
 */
public enum DirHorizontal8 implements IStringSerializable {
	NORTH(Direction.NORTH), 
	NORTH_EAST(Direction.NORTH, Direction.EAST), 
	EAST(Direction.EAST),
	SOUTH_EAST(Direction.SOUTH, Direction.EAST), 
	SOUTH(Direction.SOUTH), 
	SOUTH_WEST(Direction.SOUTH, Direction.WEST),
	WEST(Direction.WEST), 
	NORTH_WEST(Direction.NORTH, Direction.WEST);
	
	public static final EnumProperty<DirHorizontal8> prop = EnumProperty.create("facing8", DirHorizontal8.class, DirHorizontal8.values());
	private static final Direction[] dir4Mapping = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	private static final DirHorizontal8[] dir4RevMapping = new DirHorizontal8[] {null, null, DirHorizontal8.NORTH, DirHorizontal8.SOUTH, DirHorizontal8.WEST, DirHorizontal8.EAST};
	
	public boolean isOnAxis() {
		return (this.ordinal()>>1)<<1 == this.ordinal();
	}
	
	public Direction toDirection4() {
		return dir4Mapping[this.ordinal()>>1];
	}
	
	public static DirHorizontal8 fromDirection4(Direction dir) {
		return dir4RevMapping[dir.ordinal()];
	}
	
	public Direction8 toVanilla() {
		return Direction8.values()[this.ordinal()];
	}
	
	public static DirHorizontal8 fromVanilla(Direction8 dir8) {
		return values()[dir8.ordinal()];
	}
	
    public static DirHorizontal8 fromSight(Entity entity) {
        int facingInt = MathHelper.floor(entity.rotationYaw * 8.0F / 360.0F + 0.5D - 4) & 7;
        // north0 east2 south4 west6
        return values()[facingInt];
    }
    
    public DirHorizontal8 clockwise() {
    	int nextId = this.ordinal() + 1;
    	if (nextId >= DirHorizontal8.values().length)
    		nextId -= DirHorizontal8.values().length;
    	return this.values()[nextId];
    }
    
    public DirHorizontal8 anticlockwise() {
    	int nextId = this.ordinal() - 1;
    	if (nextId < 0)
    		nextId += DirHorizontal8.values().length;
    	return this.values()[nextId];
    }
	
	private final Set<Direction> directions;

	private DirHorizontal8(Direction... directionsIn) {
		this.directions = Sets.immutableEnumSet(Arrays.asList(directionsIn));
	}

	public Set<Direction> getDirections() {
		return this.directions;
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
