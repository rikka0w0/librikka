package rikka.librikka.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResultType;
//import rikka.librikka.block.BlockBase;
//import rikka.librikka.block.ISubBlock;

public class ItemBlockBase extends BlockItem {
	/**
	 * This is the only valid constructor for ItemBlockBase, if to be used with BlockBase
	 * Your own implementation should keep this constructor!
	 * @param block
	 * @param props
	 */
    public ItemBlockBase(Block block, Item.Properties props) {
    	super(block, props);
        
        this.setRegistryName(block.getRegistryName());
    }

    ///////////////////////////////////////////////////////////
    /// The IBlockState-Sensitive block placing handler
    ///////////////////////////////////////////////////////////
    protected boolean tweakVanilliaBlockPlacing() {
    	return true;
    }
    
//    public static boolean mayPlace(World world, BlockState blockStateToBePlaced, BlockPos pos, Direction sidePlacedOn, @Nullable Entity placer) {
//        Block blockIn = blockStateToBePlaced.getBlock();
//        BlockState iblockstate = world.getBlockState(pos);
//        VoxelShape voxelShape = blockStateToBePlaced.getCollisionShape(world, pos);
//        
//        if (!voxelShape.isEmpty() && ! world.checkNoEntityCollision(placer, voxelShape.withOffset(pos.getX(), pos.getY(), pos.getZ()))) {
//        	return false;
//        } else {
//        	boolean flag1 = iblockstate.getBlock().isReplaceable(world, pos) ;
//        	boolean flag2 = blockIn.canPlaceBlockOnSide(world, pos, sidePlacedOn);
//        	return flag1 && flag2;
//        }
//    }
//	
//	@Override
//    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, PlayerEntity placer, ItemStack stack) {
//		if (!tweakVanilliaBlockPlacing())
//			super.canPlaceBlockOnSide(world, pos, side, placer, stack);
//		
//		Block selected = world.getBlockState(pos).getBlock();
//		if (!block.isReplaceable(world, pos))
//            pos = pos.offset(side);
//
//		EnumHand hand = placer.getHeldItemMainhand() == stack ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
//		int meta = stack.getMetadata();
//		IBlockState blockStateToBePlaced = this.block.getStateForPlacement(world, pos, side, 0, 0, 0, meta, placer, hand);
//		
//		return mayPlace(world, blockStateToBePlaced, pos, side, (Entity)null);
//    }
	
	@Override
    //public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	public ActionResultType tryPlace(BlockItemUseContext context) {
//		if (!tweakVanilliaBlockPlacing())
			return super.tryPlace(context);
		// TODO: fix tweakVanilliaBlockPlacing()
//		BlockState iblockstate = context.getWorld().getBlockState(context.getPos());
//        BlockPos pos = context.getPos();
//        Direction facing = context.getFace();
//        PlayerEntity player = context.getPlayer();
//
//        if (!context.canPlace()) {
//            pos = pos.offset(facing);
//        }
//
//        ItemStack itemstack = context.getPlayer().getHeldItem(hand);
//        int meta = this.getMetadata(itemstack.getMetadata());
//        IBlockState blockStateToBePlaced = iblockstate.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, player, hand);
//        
//        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && mayPlace(worldIn, blockStateToBePlaced, pos, facing, (Entity)null))
//        {
//
//
//            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, blockStateToBePlaced))
//            {
//                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
//                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
//                itemstack.shrink(1);
//            }
//
//            return ActionResultType.SUCCESS;
//        }
//        else
//        {
//            return ActionResultType.FAIL;
//        }
    }
}
