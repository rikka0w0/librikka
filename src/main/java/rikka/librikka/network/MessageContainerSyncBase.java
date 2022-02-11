package rikka.librikka.network;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import rikka.librikka.ByteSerializer;
import rikka.librikka.container.ContainerSynchronizer;

public abstract class MessageContainerSyncBase {
	public static abstract class Processor<T extends MessageContainerSyncBase> {
		protected Processor() {}

		protected abstract T create();

		public <B extends ByteBuf> void toBytes(T msg, B buf) {
			buf.writeInt(msg.windowID);
			buf.writeByte(msg.type);
			buf.writeByte(msg.data.length);

			for (int i = 0; i < msg.data.length; i++)
				getSerializer().packData(buf, msg.data[i]);
		}

		public T fromBytes(ByteBuf buf) {
			T msg = create();

			msg.windowID = buf.readInt();
			msg.type = buf.readByte();
			int length = buf.readByte();
			msg.data = new Object[length];

			for (int i = 0; i < msg.data.length; i++)
				msg.data[i] = getSerializer().unpackData(buf);

			return msg;
		}

		public void handler(final T message, Supplier<NetworkEvent.Context> ctx) {
			LogicalSide side = ctx.get().getDirection().getReceptionSide();
			AbstractContainerMenu container = side.isClient() ? this.getClientPlayer().containerMenu
					: ctx.get().getSender().containerMenu;
			if (container.containerId != message.windowID)
				return;

			if (side.isServer()) {
				// Make sure the actual modification is done on the server-thread.
				ctx.get().enqueueWork(()->message.processServer(container));
			}
			else if (side.isClient()) {
				ctx.get().enqueueWork(()->message.processClient(container));
			}

			ctx.get().setPacketHandled(true);
		}

		protected ByteSerializer getSerializer() {
			return ByteSerializer.instance;
		}

		protected abstract Player getClientPlayer();
	}

	protected final static byte EVENT_CUSTOM = 0;
	protected final static byte EVENT_SYNC = 1;

	// MessageData
	protected int windowID;
	protected byte type;
	protected Object[] data;

	// Receiver side constructor
	protected MessageContainerSyncBase() {
	}

	// Sender side constructor
	protected MessageContainerSyncBase(int windowID, byte type, Object[] data) {
		this.windowID = windowID;
		this.type = type;
		this.data = data;
	}

	/**
	 * Process the message on server side (Handle messages from client)
	 */
	protected void processServer(AbstractContainerMenu container) {
		switch (type) {
		case EVENT_SYNC:
			break;
		case EVENT_CUSTOM:
			if (container instanceof ICustomContainerEventServerHandler)
				((ICustomContainerEventServerHandler) container).onDataArrivedFromClient(data);
			break;
		}
	}

	/**
	 * Process the message on client side (Handle messages from server)
	 * <br>
	 * This class have to be visible to the dedicated server even the server doesn't need it at all
	 */
	protected void processClient(AbstractContainerMenu container) {
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
}
