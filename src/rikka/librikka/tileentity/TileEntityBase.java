package rikka.librikka.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityBase extends TileEntity {
    @Override
    public void onChunkUnload() {
        this.invalidate();
    }

    /**
     * Controls whether Mincraft should replace the existing TileEntity or not when a BlockState change happens.
     * Use with caution as this will leave straggler TileEntities if not used properly.
     * <p>
     * Examples:
     * <p>
     * 1. The BlockState only contains "type" information:  oldState.getBlock() != newState.getBlock()
     * <p>
     * 2. The BlockState contains only "type" but also their information like "facing": compare difference of property "type" between oldState and newState
     * <p>
     * 3. If you are not sure, put: return oldState != newState;
     */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState != newState;
        //return (oldState.getBlock() != newState.getBlock());	//Was "return !isVanilla || (oldBlock != newBlock);" in 1.7.10
    }


    protected void markTileEntityForS2CSync() {
        this.world.notifyBlockUpdate(this.getPos(), this.world.getBlockState(this.getPos()), this.world.getBlockState(this.getPos()), 2);
    }

    @SideOnly(Side.CLIENT)
    protected void markForRenderUpdate() {
        this.world.notifyBlockUpdate(this.getPos(), this.world.getBlockState(this.getPos()), this.world.getBlockState(this.getPos()), 1);
    }


    // When the world loads from disk, the server needs to send the TileEntity information to the client
    //  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
    //  getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
    //  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet

    //Sync
    public void prepareS2CPacketData(NBTTagCompound nbt) {
    }

    @SideOnly(Side.CLIENT)
    public void onSyncDataFromServerArrived(NBTTagCompound nbt) {
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        //System.out.println("[DEBUG]:Server sent tile sync packet");
        NBTTagCompound tagCompound = new NBTTagCompound();
        this.prepareS2CPacketData(tagCompound);
        return new SPacketUpdateTileEntity(this.pos, 1, tagCompound);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

        if (this.world.isRemote) {
            //System.out.println("[DEBUG]:Client recived INDIVIDUAL tileSync packet");	//Debug

            //This is supposed to be Client ONLY!
            //SPacketUpdateTileEntity starts with S, means that this packet is sent from server to client
            this.onSyncDataFromServerArrived(pkt.getNbtCompound());
        }
    }

    /**
     * Chunk Sync
     */
    @Override
    public final NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();

        //Prepare custom payload
        this.prepareS2CPacketData(nbt);

        return nbt;
    }

    /**
     * Called when the chunk's TE update tag, gotten from {@link #getUpdateTag()}, is received on the client.
     * <p>
     * Used to handle this tag in a special way. By default this simply calls {@link #readFromNBT(NBTTagCompound)}.
     * This function should only be called by the client thread (I think
     *
     * @param tag The {@link NBTTagCompound} sent from {@link #getUpdateTag()}
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);

        if (this.world.isRemote) {
            //System.out.println("[DEBUG]:Client recived CHUNK tileSync packet");	//Debug

            this.onSyncDataFromServerArrived(tag);
        }
    }
}
