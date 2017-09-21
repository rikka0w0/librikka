package rikka.librikka.item;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.block.BlockBase;
import rikka.librikka.block.ISubBlock;

public class ItemBlockBase extends ItemBlock {
    public ItemBlockBase(Block block) {
        super(block);

        boolean hasSubBlocks = block instanceof ISubBlock;

        if (!(block instanceof BlockBase))
            throw new RuntimeException("ItemBlockBase should be used with BlockBase!");

		this.setHasSubtypes(hasSubBlocks);

        if (hasSubBlocks)
			setMaxDamage(0);    //The item can not be damaged
        
        this.setRegistryName(block.getRegistryName());
    }

    @Override
    public final String getUnlocalizedName(ItemStack itemstack) {
        if (getHasSubtypes()) {
            BlockBase blockBase = (BlockBase) getBlock();
            String[] subBlockUnlocalizedNames = ((ISubBlock) blockBase).getSubBlockUnlocalizedNames();
            return getUnlocalizedName() + "." + subBlockUnlocalizedNames[itemstack.getItemDamage()];
        } else {
            return getUnlocalizedName();
        }
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    @Override
    public final int getMetadata(int damage) {
        if (getHasSubtypes()) {
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public final String getUnlocalizedNameInefficiently(ItemStack stack) {
        String prevName = super.getUnlocalizedNameInefficiently(stack);
        String domain = this.getRegistryName().getResourceDomain();
        return "tile." + domain + ":" + prevName.substring(5);
    }
    
    
    ///////////////////////////////////////////////////////////
    /// The IBlockState-Sensitive block placing handler
    ///////////////////////////////////////////////////////////
    protected boolean tweakVanilliaBlockPlacing() {
    	return true;
    }
    
    public static boolean mayPlace(World world, IBlockState blockStateToBePlaced, BlockPos pos, EnumFacing sidePlacedOn, @Nullable Entity placer) {
        Block blockIn = blockStateToBePlaced.getBlock();
    	IBlockState iblockstate = world.getBlockState(pos);
        AxisAlignedBB axisalignedbb = blockStateToBePlaced.getCollisionBoundingBox(world, pos);
        
        if (axisalignedbb != Block.NULL_AABB && ! world.checkNoEntityCollision(axisalignedbb.offset(pos), placer)) {
        	return false;
        } else {
        	boolean flag1 = iblockstate.getBlock().isReplaceable(world, pos) ;
        	boolean flag2 = blockIn.canPlaceBlockOnSide(world, pos, sidePlacedOn);
        	return flag1 && flag2;
        }
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer placer, ItemStack stack) {
		if (!tweakVanilliaBlockPlacing())
			super.canPlaceBlockOnSide(world, pos, side, placer, stack);
		
		Block selected = world.getBlockState(pos).getBlock();
		if (!block.isReplaceable(world, pos))
            pos = pos.offset(side);

		EnumHand hand = placer.getHeldItemMainhand() == stack ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		int meta = stack.getMetadata();
		IBlockState blockStateToBePlaced = this.block.getStateForPlacement(world, pos, side, 0, 0, 0, meta, placer, hand);
		
		return mayPlace(world, blockStateToBePlaced, pos, side, (Entity)null);
    }
	
	@Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!tweakVanilliaBlockPlacing())
			super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		
		IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);
        int meta = this.getMetadata(itemstack.getMetadata());
        IBlockState blockStateToBePlaced = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, player, hand);
        
        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && mayPlace(worldIn, blockStateToBePlaced, pos, facing, (Entity)null))
        {


            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, blockStateToBePlaced))
            {
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
}
