package io.github.steveplays28.noisium.server.world;

import com.mojang.datafixers.DataFixer;
import io.github.steveplays28.noisium.Noisium;
import io.github.steveplays28.noisium.util.world.chunk.ChunkUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoisiumServerWorldChunkManager {
	private final ServerWorld serverWorld;
	private final ChunkGenerator chunkGenerator;
	private final PointOfInterestStorage pointOfInterestStorage;
	private final VersionedChunkStorage versionedChunkStorage;
	private final Executor threadPoolExecutor;
	public final Map<ChunkPos, WorldChunk> loadedWorldChunks;

	public NoisiumServerWorldChunkManager(@NotNull ServerWorld serverWorld, @NotNull ChunkGenerator chunkGenerator, @NotNull Path worldDirectoryPath, DataFixer dataFixer) {
		this.serverWorld = serverWorld;
		this.chunkGenerator = chunkGenerator;

		this.pointOfInterestStorage = new PointOfInterestStorage(
				worldDirectoryPath.resolve("poi"), dataFixer, true, serverWorld.getRegistryManager(), serverWorld);
		this.versionedChunkStorage = new NoisiumServerVersionedChunkStorage(worldDirectoryPath.resolve("region"), dataFixer, true);
		this.threadPoolExecutor = Executors.newFixedThreadPool(5);
		this.loadedWorldChunks = new HashMap<>();
	}

	/**
	 * Loads the chunk at the specified position, returning the loaded chunk when done. Returns the chunk from the <code>loadedChunks</code> cache if available.
	 *
	 * @param chunkPos The position at which to load the chunk.
	 * @return The loaded chunk.
	 */
	@Nullable
	public WorldChunk getChunk(ChunkPos chunkPos) {
		if (loadedWorldChunks.containsKey(chunkPos)) {
			return loadedWorldChunks.get(chunkPos);
		}

		ProtoChunk protoChunk;
		var fetchedNbtData = getNbtDataAtChunkPosition(chunkPos);
		if (fetchedNbtData == null) {
			// TODO: Schedule ProtoChunk worldgen
			protoChunk = new ProtoChunk(
					chunkPos, UpgradeData.NO_UPGRADE_DATA, serverWorld, serverWorld.getRegistryManager().get(RegistryKeys.BIOME), null);
			return new WorldChunk(serverWorld, protoChunk,
					chunkToAddEntitiesTo -> serverWorld.addEntities(EntityType.streamFromNbt(protoChunk.getEntities(), serverWorld))
			);
		}

		// TODO: Mark chunk as tickable
		var chunkFuture = CompletableFuture.supplyAsync(
				() -> ChunkSerializer.deserialize(serverWorld, pointOfInterestStorage, chunkPos, fetchedNbtData));

		try {
			var fetchedChunk = chunkFuture.get();
			var fetchedWorldChunk = new WorldChunk(serverWorld, fetchedChunk,
					chunkToAddEntitiesTo -> serverWorld.addEntities(EntityType.streamFromNbt(fetchedChunk.getEntities(), serverWorld))
			);

			loadedWorldChunks.put(chunkPos, fetchedWorldChunk);
			ChunkUtil.sendWorldChunkToPlayer(serverWorld, fetchedWorldChunk);
			return fetchedWorldChunk;
		} catch (Exception ex) {
			// TODO
			return null;
		}
	}

	/**
	 * Gets all {@link WorldChunk}s around the specified chunk, using a square radius.
	 *
	 * @param chunkPos The center {@link ChunkPos}.
	 * @param radius   A square radius of chunks.
	 * @return All the {@link WorldChunk}s around the specified chunk, using a square radius.
	 */
	public @NotNull Map<@NotNull ChunkPos, @Nullable WorldChunk> getChunksInRadius(@NotNull ChunkPos chunkPos, int radius) {
		var chunks = new HashMap<@NotNull ChunkPos, @Nullable WorldChunk>();

		for (int chunkPosX = chunkPos.x - radius; chunkPosX < chunkPos.x + radius; chunkPosX++) {
			for (int chunkPosZ = chunkPos.z - radius; chunkPosZ < chunkPos.z + radius; chunkPosZ++) {
				chunks.put(new ChunkPos(chunkPosX, chunkPosZ), getChunk(chunkPos));
			}
		}

		return chunks;
	}

	private @Nullable NbtCompound getNbtDataAtChunkPosition(ChunkPos chunkPos) {
		try {
			var fetchedNbtCompoundOptionalFuture = versionedChunkStorage.getNbt(chunkPos).get();
			if (fetchedNbtCompoundOptionalFuture.isPresent()) {
				return fetchedNbtCompoundOptionalFuture.get();
			}
		} catch (Exception ex) {
			Noisium.LOGGER.error("Error occurred while fetching NBT data for chunk at {}", chunkPos);
		}

		return null;
	}
}
