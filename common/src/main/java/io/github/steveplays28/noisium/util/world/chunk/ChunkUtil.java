package io.github.steveplays28.noisium.util.world.chunk;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

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

	public static void sendWorldChunksToPlayer(@NotNull ServerWorld serverWorld, @NotNull Map<ChunkPos, WorldChunk> worldChunks) {
		// TODO: Send a whole batch of chunks to the player at once to save on network traffic
		for (var worldChunk : worldChunks.values()) {
			sendWorldChunkToPlayer(serverWorld, worldChunk);
		}
	}

	@SuppressWarnings("ForLoopReplaceableByForEach")
	public static void sendLightUpdateToPlayers(@NotNull List<ServerPlayerEntity> players, LightingProvider lightingProvider, ChunkPos chunkPos, BitSet skyLightBits, BitSet blockLightBits) {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).networkHandler.sendPacket(new LightUpdateS2CPacket(chunkPos, lightingProvider, skyLightBits, blockLightBits));
		}
	}
}
