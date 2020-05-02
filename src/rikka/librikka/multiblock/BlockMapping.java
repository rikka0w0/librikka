package rikka.librikka.multiblock;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;

public class BlockMapping {
    public final BlockState state;
    public final BlockState state2;
    
    /**
     * The MultiBlockStructure controller only checks properties from getStateFromMeta, other properties and UnlistedProperties will be ignored
     * @param state
     * @param state2
     */
    public BlockMapping(BlockState state, BlockState state2) {
        this.state = state;
        this.state2 = state2;
    }
    
    /**
     * Override this function to ignore certain property
     * @param state
     * @return
     */
    protected boolean isDifferent(BlockState state) {
    	 return this.state != state;
    }
    
    protected boolean isDifferent2(BlockState state) {
    	return state2 != state;
    }
    
    protected BlockState getStateForRestore(@Nullable TileEntity tileEntity) {
    	return state;
    }
}