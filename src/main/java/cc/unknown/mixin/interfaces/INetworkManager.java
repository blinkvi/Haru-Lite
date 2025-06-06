package cc.unknown.mixin.interfaces;

import net.minecraft.network.Packet;

public interface INetworkManager {
	void addToSendQueueUnregistered(Packet packet);

	void addToReceiveQueueUnregistered(Packet packet);
	
	void addToReceiveQueue(Packet packet);
}