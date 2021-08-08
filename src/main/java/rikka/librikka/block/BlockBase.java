package rikka.librikka.block;

import java.lang.reflect.Constructor;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import rikka.librikka.item.ItemBlockBase;

public abstract class BlockBase extends Block {
	@Nullable
	private final ItemBlockBase itemBlock;

	public BlockBase(String regName, Block.Properties props, Item.Properties itemProps) {
		this(regName, props, ItemBlockBase.class, itemProps);
	}

	public BlockBase(String regName, Block.Properties props, CreativeModeTab group) {
		this(regName, props, (new Item.Properties()).tab(group));
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
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
           worldIn.removeBlockEntity(pos);
        }
    }

    @Override
	@Nullable
	public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos pos) {
		if (!(this instanceof EntityBlock))
			return null;

		BlockEntity blockEntity = level.getBlockEntity(pos);
		return blockEntity instanceof MenuProvider ? (MenuProvider) blockEntity : null;
	}
    /**
     * Defines the properties needed for the BlockState
     * @param builder
     */
//    @Override
//    protected void fillStateContainer(StateDefinition.Builder<Block, BlockState> builder) {
//      builder.add(FACING, WATERLOGGED);
//    }
}