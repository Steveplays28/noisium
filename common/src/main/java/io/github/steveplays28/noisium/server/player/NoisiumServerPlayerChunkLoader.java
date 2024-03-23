package io.github.steveplays28.noisium.server.player;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.steveplays28.noisium.extension.world.server.NoisiumServerWorldExtension;
import io.github.steveplays28.noisium.util.world.chunk.ChunkUtil;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiFunction;

public class NoisiumServerPlayerChunkLoader {
	private final Map<Integer, Vec3d> previousPlayerPositions;
	private final Executor threadPoolExecutor;

	public NoisiumServerPlayerChunkLoader() {
		this.previousPlayerPositions = new HashMap<>();
		this.threadPoolExecutor = Executors.newFixedThreadPool(
				1, new ThreadFactoryBuilder().setNameFormat("Noisium Server Player Chunk Loader %d").build());

		// TODO: Send chunks to player on join
		PlayerEvent.PLAYER_JOIN.register(player -> previousPlayerPositions.put(player.getId(), player.getPos()));
		PlayerEvent.PLAYER_QUIT.register(player -> previousPlayerPositions.remove(player.getId()));
		TickEvent.ServerLevelTick.SERVER_LEVEL_POST.register(
				instance -> tick(
						instance, ((NoisiumServerWorldExtension) instance).noisium$getServerWorldChunkManager()::getChunksInRadiusAsync));
	}

	// TODO: Enable ticking/update chunk tracking in ServerEntityManager
	@SuppressWarnings("ForLoopReplaceableByForEach")
	private void tick(@NotNull ServerWorld serverWorld, @NotNull BiFunction<ChunkPos, Integer, Map<ChunkPos, CompletableFuture<WorldChunk>>> worldChunksSupplier) {
		var players = serverWorld.getPlayers();
		if (players.size() == 0 || previousPlayerPositions.size() == 0) {
			return;
		}

		for (int i = 0; i < players.size(); i++) {
			var player = players.get(i);
			var playerBlockPos = player.getBlockPos();
			if (!playerBlockPos.isWithinDistance(previousPlayerPositions.get(player.getId()), 16d)) {
				var worldChunks = worldChunksSupplier.apply(new ChunkPos(playerBlockPos), 14);

				ChunkUtil.sendWorldChunksToPlayerAsync(serverWorld, new ArrayList<>(worldChunks.values()));
				CompletableFuture.runAsync(() -> player.networkHandler.sendPacket(
						new ChunkRenderDistanceCenterS2CPacket(
								ChunkSectionPos.getSectionCoord(playerBlockPos.getX()),
								ChunkSectionPos.getSectionCoord(playerBlockPos.getZ())
						)), threadPoolExecutor);
				previousPlayerPositions.put(player.getId(), player.getPos());
			}
		}
	}
}
