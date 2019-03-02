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

/**
 * A more automatic and object-oriented GuiHandler, ID 0 to 5 is reserved for EnumFacing, and ID>5 will be considered as custom Gui/Container
 *
 */
public class AutoGuiHandler implements IGuiHandler{
	@Override
	public final Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		if (ID > 5)
			return getContainer(ID, player, world, pos);
		
        TileEntity te = world.getTileEntity(pos);
        
        if (te instanceof IGuiProviderTile)
            return ((IGuiProviderTile) te).getContainer(player, EnumFacing.getFront(ID));
        
		return null;
	}

	@Override
	public final Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Object guiContainer = getServerGuiElement(ID, player, world, x, y, z);

		if (guiContainer instanceof IContainerWithGui)
			return ((IContainerWithGui) guiContainer).createGui();

		if (ID > 5)
			return getGui(ID, player, world, new BlockPos(x, y, z));

		return null;
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
