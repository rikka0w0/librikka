package rikka.librikka.block;

import java.lang.reflect.Constructor;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
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
    
    /////////////////////////////
    /// 1.15.2 Compatibility
    /////////////////////////////
    public ActionResultType onBlockActivatedImpl(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rtResult) {
    	return ActionResultType.PASS;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    	return onBlockActivatedImpl(state, worldIn, pos, player, handIn, hit) == ActionResultType.SUCCESS;
    }
}