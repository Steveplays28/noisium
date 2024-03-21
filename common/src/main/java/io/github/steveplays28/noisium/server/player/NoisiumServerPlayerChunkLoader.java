package io.github.steveplays28.noisium.server.player;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.steveplays28.noisium.extension.world.server.NoisiumServerWorldExtension;
import io.github.steveplays28.noisium.util.world.chunk.ChunkUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class NoisiumServerPlayerChunkLoader {
	private final Map<Integer, Vec3d> previousPlayerPositions = new HashMap<>();

	public NoisiumServerPlayerChunkLoader() {
		// TODO: Send chunks to player on join
		PlayerEvent.PLAYER_JOIN.register(player -> previousPlayerPositions.put(player.getId(), player.getPos()));
		PlayerEvent.PLAYER_QUIT.register(player -> previousPlayerPositions.remove(player.getId()));
		TickEvent.ServerLevelTick.SERVER_LEVEL_POST.register(
				instance -> tick(instance, ((NoisiumServerWorldExtension) instance).noisium$getServerWorldChunkManager()::getChunk));
	}

	private void tick(@NotNull ServerWorld serverWorld, @NotNull Function<ChunkPos, WorldChunk> worldChunkSupplier) {
		var players = serverWorld.getPlayers();
		if (players.size() == 0 || previousPlayerPositions.size() == 0) {
			return;
		}

		for (int i = 0; i < players.size(); i++) {
			var player = players.get(i);
			var playerBlockPos = player.getBlockPos();
			if (!playerBlockPos.isWithinDistance(previousPlayerPositions.get(player.getId()), 16d)) {
				ChunkUtil.sendWorldChunkToPlayer(
						serverWorld, worldChunkSupplier.apply(new ChunkPos(playerBlockPos)));
				previousPlayerPositions.put(i, player.getPos());
			}
		}
	}
}
