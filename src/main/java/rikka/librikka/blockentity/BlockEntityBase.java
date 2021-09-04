package rikka.librikka.blockentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.Constants;

public abstract class BlockEntityBase extends BlockEntity {
	public BlockEntityBase(BlockEntityType<?> teType, BlockPos pos, BlockState blockState) {
		super(teType, pos, blockState);
	}

	// TODO: Check onChunkUnload()
    @Override
    public void onChunkUnloaded() {
        this.setRemoved();
    }

    protected void markTileEntityForS2CSync() {
    	setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.DEFAULT);
    }

    @OnlyIn(Dist.CLIENT)
    protected void markForRenderUpdate() {
    	ModelDataManager.requestModelDataRefresh(this);
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.RERENDER_MAIN_THREAD);
    }


    // When the world loads from disk, the server needs to send the BlockEntity information to the client
    //  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
    //  getUpdatePacket() and onDataPacket() are used for one-at-a-time BlockEntity updates
    //  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet

    //Sync
    public void prepareS2CPacketData(CompoundTag nbt) {
    }

    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundTag nbt) {
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        //System.out.println("[DEBUG]:Server sent tile sync packet");
    	CompoundTag tagCompound = new CompoundTag();
        this.prepareS2CPacketData(tagCompound);
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, tagCompound);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {

        if (this.level.isClientSide) {
            //System.out.println("[DEBUG]:Client recived INDIVIDUAL tileSync packet");	//Debug

            //This is supposed to be Client ONLY!
            //SPacketUpdateTileEntity starts with S, means that this packet is sent from server to client
            this.onSyncDataFromServerArrived(pkt.getTag());
        }
    }

    /**
     * LevelChunk Sync
     */
    @Override
    public final CompoundTag getUpdateTag() {
    	CompoundTag nbt = super.getUpdateTag();

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
    @OnlyIn(Dist.CLIENT)
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);

        if (this.level.isClientSide) {
            //System.out.println("[DEBUG]:Client recived CHUNK tileSync packet");	//Debug

            this.onSyncDataFromServerArrived(tag);
        }
    }

    protected void collectModelData(ModelDataMap.Builder builder) {

    }

    @Override
    public final IModelData getModelData() {
    	ModelDataMap.Builder builder = new ModelDataMap.Builder();
    	collectModelData(builder);
    	return builder.build();
    }

    // TODO: Fix BlockEntity::getViewDistance
    @OnlyIn(Dist.CLIENT)
    public double getViewDistance() {
        return 100000;
    }
}
