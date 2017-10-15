package rikka.librikka.multiblock;

import net.minecraft.block.state.IBlockState;

public class BlockMapping {
    public final IBlockState state;
    public final IBlockState state2;
    
    /**
     * The MultiBlockStructure controller only checks properties from getStateFromMeta, other properties and UnlistedProperties will be ignored
     * @param state
     * @param state2
     */
    public BlockMapping(IBlockState state, IBlockState state2) {
        this.state = state;
        this.state2 = state2;
    }
    
    /**
     * Override this function to ignore certain property
     * @param state
     * @return
     */
    protected boolean isDifferent(IBlockState state) {
    	 return this.state != state;
    }
    
    protected boolean isDifferent2(IBlockState state) {
    	return state2 != state;
    }
}