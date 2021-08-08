package rikka.librikka.multiblock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;

public class BlockMapping {
    private final BlockState fromState;
    private final BlockState toState;
    
    public BlockMapping(BlockState toState) {
    	this(Blocks.AIR.defaultBlockState(), toState);
    }
    
    /**
     * The MultiBlockStructure controller only checks properties from getStateFromMeta, other properties and UnlistedProperties will be ignored
     * @param state
     * @param state2
     */
    public BlockMapping(BlockState fromState, BlockState toState) {
        this.fromState = fromState;
        this.toState = toState;
    }
    
    /**
     * Override this function to ignore certain property
     * @param state
     * @return true to stop multi-block structure construction
     */
    protected boolean cancelPlacement(BlockState state) {
    	 return this.fromState != state;
    }
    
    protected boolean cancelRestore(BlockState state) {
    	return toState != state;
    }
    
    protected BlockState getStateForPlacement(Direction facing) {
    	return toState;
    }
    
    protected BlockState getStateForRestore(Direction facing) {
    	return fromState;
    }
    
	@Override
    public String toString() {
    	return fromState.toString() + " -> " + toState.toString();
    }
}