package io.github.steveplays28.noisium.server.player;

import dev.architectury.event.events.common.TickEvent;
import io.github.steveplays28.noisium.extension.world.server.NoisiumServerWorldExtension;
import io.github.steveplays28.noisium.util.world.chunk.ChunkUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NoisiumServerPlayerChunkLoader {
	private List<ServerPlayerEntity> previousTickPlayers = new ArrayList<>();

	public NoisiumServerPlayerChunkLoader() {
		TickEvent.ServerLevelTick.SERVER_LEVEL_POST.register(
				instance -> tick(instance, ((NoisiumServerWorldExtension) instance).noisium$getServerWorldChunkManager()::getChunk));
	}

	private void tick(@NotNull ServerWorld serverWorld, @NotNull Function<ChunkPos, WorldChunk> worldChunkSupplier) {
		var players = serverWorld.getPlayers();

		for (int i = 0; i < players.size(); i++) {
			var playerBlockPos = players.get(i).getBlockPos();
			if (!playerBlockPos.equals(previousTickPlayers.get(i).getBlockPos())) {
				// TODO: Add lightProvider reference
				ChunkUtil.sendWorldChunkToPlayer(
						serverWorld, worldChunkSupplier.apply(new ChunkPos(playerBlockPos.getX(), playerBlockPos.getZ())));
			}
		}

		previousTickPlayers = players;
	}
}
