package rikka.librikka.block;

import java.lang.reflect.Constructor;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rikka.librikka.item.ItemBlockBase;

public abstract class BlockBase extends Block {
	@Nullable
	private final ItemBlockBase itemBlock;
	    
	public BlockBase(String regName, Block.Properties props, Item.Properties itemProps) {
		this(regName, props, ItemBlockBase.class, itemProps);
	}
	
	public BlockBase(String regName, Block.Properties props, ItemGroup group) {
		this(regName, props, (new Item.Properties()).group(group));
	}
	
    public BlockBase(String regName, Block.Properties props, Class<? extends ItemBlockBase> itemBlockClass, Item.Properties itemProps) {
        super(props);
        setRegistryName(regName);                //Key!
        // localization key: block.<MODID>.<name>
        // Do setDefaultState() in the constructor!
        
        if (itemBlockClass == null) {
        	itemBlock = null;
        } else {
            try {
                Constructor<? extends ItemBlockBase> constructor = itemBlockClass.getConstructor(Block.class, Item.Properties.class);
                itemBlock = constructor.newInstance(this, itemProps);
            } catch (Exception e) {
                throw new RuntimeException("Invalid ItemBlock constructor!");
            }
        }
    }
    
    @Override
    public Item asItem() {
    	return this.itemBlock;
    }
    
	// Was TileEntityBase::shouldRefresh()
	// Different different block are now distinguished by the difference of Block instances
	// blockState only represents different state of a block, but anyway it is still the same block!
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
           worldIn.removeTileEntity(pos);
        }
    }
    
    /**
     * Defines the properties needed for the BlockState
     * @param builder
     */
//    @Override
//    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
//      builder.add(FACING, WATERLOGGED);
//    }
}