package io.github.steveplays28.noisium.util.world.chunk;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

public class ChunkUtil {
	/**
	 * Sends a {@link WorldChunk} to all players in the specified world.
	 *
	 * @param serverWorld The world the {@link WorldChunk} resides in.
	 * @param worldChunk  The {@link WorldChunk}.
	 */
	public static void sendWorldChunkToPlayer(@NotNull ServerWorld serverWorld, @NotNull WorldChunk worldChunk) {
		var chunkDataS2CPacket = new ChunkDataS2CPacket(worldChunk, serverWorld.getLightingProvider(), null, null);

		for (int i = 0; i < serverWorld.getPlayers().size(); i++) {
			serverWorld.getPlayers().get(i).sendChunkPacket(worldChunk.getPos(), chunkDataS2CPacket);
		}
	}
}
