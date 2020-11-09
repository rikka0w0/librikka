package rikka.librikka.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public abstract class TileEntityBase extends TileEntity {
	protected final TileEntityType<?> teType;
	
	public TileEntityBase(TileEntityType<?> teType) {
		super(teType);
		this.teType = teType;
	}
	
	public TileEntityBase(String namespace) {
		super(null);
		TileEntityType<?> teType = TileEntityHelper.getTeType(namespace, this.getClass());
		this.teType = teType;
//		setType(teType);
//		for (Field f:TileEntity.class.getDeclaredFields()) {
//			if (f.getType() == TileEntityType.class) {
//				try {
//					f.setAccessible(true);
//					f.set(this, teType);
//					f.setAccessible(false);
//					break;
//				} catch (IllegalArgumentException | IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}

	protected void setType(TileEntityType <?extends TileEntity> teType) {
		ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, this, teType, "field_200663_e");
	}
	
	@Override
	public TileEntityType<?> getType() {
		return this.teType;
	}
	
	// TODO: Check onChunkUnload()
    @Override
    public void onChunkUnloaded() {
        this.remove();
    }

    protected void markTileEntityForS2CSync() {
    	markDirty();
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    @OnlyIn(Dist.CLIENT)
    protected void markForRenderUpdate() {
    	ModelDataManager.requestModelDataRefresh(this);
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }


    // When the world loads from disk, the server needs to send the TileEntity information to the client
    //  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
    //  getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
    //  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet

    //Sync
    public void prepareS2CPacketData(CompoundNBT nbt) {
    }

    @OnlyIn(Dist.CLIENT)
    public void onSyncDataFromServerArrived(CompoundNBT nbt) {
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        //System.out.println("[DEBUG]:Server sent tile sync packet");
    	CompoundNBT tagCompound = new CompoundNBT();
        this.prepareS2CPacketData(tagCompound);
        return new SUpdateTileEntityPacket(this.pos, 0, tagCompound);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {

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
    public final CompoundNBT getUpdateTag() {
    	CompoundNBT nbt = super.getUpdateTag();

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
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);

        if (this.world.isRemote) {
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
}
