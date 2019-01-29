package rikka.librikka.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.item.ItemBlockBase;

import java.lang.reflect.Constructor;

public abstract class BlockBase extends Block {
    public final ItemBlockBase itemBlock;

    public BlockBase(String unlocalizedName, Material material, Class<? extends ItemBlockBase> itemBlockClass) {
        super(material);
        setUnlocalizedName(unlocalizedName);
        setRegistryName(unlocalizedName);                //Key!
        setDefaultState(getBaseState(this.blockState.getBaseState()));
        
        try {
            Constructor constructor = itemBlockClass.getConstructor(Block.class);
            itemBlock = (ItemBlockBase) constructor.newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException("Invalid ItemBlock constructor!");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.itemBlock.getHasSubtypes()) {
            for (int ix = 0; ix < ((ISubBlock) this).getSubBlockUnlocalizedNames().length; ix++)
                subItems.add(new ItemStack(this, 1, ix));
        } else {
        	subItems.add(new ItemStack(this));
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
    
    //BlockState --------------------------------------------------------------------
    //createBlockState, setDefaultBlockState
    
    /**
     * Called during class construction
     * @param firstValidState
     * @return the base state
     */
    protected IBlockState getBaseState(IBlockState firstValidState) {
		return firstValidState;
    }
}