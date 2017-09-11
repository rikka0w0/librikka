package rikka.librikka;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.container.IContainerWithGui;
import rikka.librikka.tileentity.IGuiProviderTile;

public class AutoGuiHandler implements IGuiHandler{
	@Override
	public final Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        
        if (te instanceof IGuiProviderTile)
            return ((IGuiProviderTile) te).getContainer(player, EnumFacing.getFront(ID));
        
		return getContainer(ID, player, world, pos);
	}

	@Override
	public final Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    	Object guiContainer = getServerGuiElement(ID, player, world, x, y, z);
    	if (guiContainer instanceof IContainerWithGui)
    		return ((IContainerWithGui) guiContainer).createGui();
    	
		return getGui(ID, player, world, new BlockPos(x, y, z));
	}
	
	/////////////////////////////////////////////////
	/// Custom Processors, Override when necessary
	/////////////////////////////////////////////////
	protected Container getContainer(int ID, EntityPlayer player, World world, BlockPos pos) {
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	protected GuiScreen getGui(int ID, EntityPlayer player, World world, BlockPos pos) {
		return null;
	}
}
