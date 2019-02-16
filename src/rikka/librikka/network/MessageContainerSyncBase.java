package rikka.librikka.network;

import java.util.Iterator;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rikka.librikka.ByteSerializer;
import rikka.librikka.container.ContainerSynchronizer;

public abstract class MessageContainerSyncBase implements IMessage {
	protected final static byte EVENT_CUSTOM = 0; 
	protected final static byte EVENT_SYNC = 1;

	/**
	 * Compulsory constructor
	 */
	public MessageContainerSyncBase() {
	}

	// MessageData
	protected int windowID;
	protected byte type;
	protected Object[] data;

	protected MessageContainerSyncBase(int windowID, byte type, Object[] data) {
		this.windowID = windowID;
		this.type = type;
		this.data = data;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.windowID);
		buf.writeByte(this.type);
		buf.writeByte(this.data.length);

		for (int i = 0; i < this.data.length; i++)
			this.getSerializer().packData(buf, this.data[i]);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.windowID = buf.readInt();
		this.type = buf.readByte();
		int length = buf.readByte();
		this.data = new Object[length];

		for (int i = 0; i < this.data.length; i++)
			this.data[i] = this.getSerializer().unpackData(buf);
	}

	protected ByteSerializer getSerializer() {
		return ByteSerializer.instance;
	}

	/**
	 * Process the message on server side (Handle messages from client)
	 */
	public static abstract class HandlerServer<MSG extends MessageContainerSyncBase> implements IMessageHandler<MSG, IMessage> {
		/**
		 * For all players with the given container opened
		 */
		protected void process(Container container, byte type, Object[] data) {			
			switch (type) {
			case EVENT_SYNC:
				break;
			case EVENT_CUSTOM:
				if (container instanceof ICustomContainerEventServerHandler)
					((ICustomContainerEventServerHandler) container).onDataArrivedFromClient(data);
				break;
			}
		}

		@Override
		public IMessage onMessage(MSG message, MessageContext ctx) {
			// Server
			MinecraftServer server = ctx.getServerHandler().player.mcServer;
			int windowID = message.windowID;
			byte type = message.type;
			Object[] data = message.data;

			// Make sure the actual modification is done on the server-thread.
			server.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Iterator<EntityPlayerMP> playerListIterator = server.getPlayerList().getPlayers().iterator();
					while (playerListIterator.hasNext()) {
						EntityPlayerMP player = playerListIterator.next();

						if (player.openContainer.windowId == windowID)
							process(player.openContainer, type, data);
					} // while()
				}// run()
			});

			// Reply nothing
			return null;
		}
	}

	/** 
	 * Process the message on client side (Handle messages from server)
	 * <br>
	 * This class have to be visible to the dedicated server even the server doesn't need it at all
	 */
	public static abstract class HandlerClient<MSG extends MessageContainerSyncBase> implements IMessageHandler<MSG, IMessage> {
		protected void process(Container container, byte type, Object[] data) {			
			switch (type) {
			case EVENT_SYNC:
				ContainerSynchronizer.syncClientFields(data, container);
				break;
			case EVENT_CUSTOM:
				if (container instanceof ICustomContainerEventClientHanlder)
					((ICustomContainerEventClientHanlder) container).onDataArrivedFromServer(data);
				break;
			}
		}

		@Override
		public IMessage onMessage(MSG message, MessageContext ctx) {
			int windowID = message.windowID;
			byte type = message.type;
			Object[] data = message.data;

			//Client
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Container container = Minecraft.getMinecraft().player.openContainer;
					
					if (container.windowId == windowID)
						process(container, type, data);
				}
			});

			//Reply nothing
			return null;
		}
	}
}
