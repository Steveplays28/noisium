package io.github.steveplays28.noisium.server.world;

import com.mojang.datafixers.DataFixer;
import io.github.steveplays28.noisium.Noisium;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class NoisiumServerWorldChunkManager {
	private final ServerWorld serverWorld;
	private final ChunkGenerator chunkGenerator;
	private final PointOfInterestStorage pointOfInterestStorage;
	private final VersionedChunkStorage versionedChunkStorage;

	public NoisiumServerWorldChunkManager(@NotNull ServerWorld serverWorld, @NotNull ChunkGenerator chunkGenerator, @NotNull Path worldDirectoryPath, DataFixer dataFixer) {
		this.serverWorld = serverWorld;
		this.chunkGenerator = chunkGenerator;

		this.pointOfInterestStorage = new PointOfInterestStorage(
				worldDirectoryPath.resolve("poi"), dataFixer, true, serverWorld.getRegistryManager(), serverWorld);
		this.versionedChunkStorage = new NoisiumServerVersionedChunkStorage(worldDirectoryPath.resolve("region"), dataFixer, true);
	}

	public Chunk getChunk(ChunkPos chunkPos) {
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
		protoChunk = ChunkSerializer.deserialize(serverWorld, pointOfInterestStorage, chunkPos, fetchedNbtData);
		return new WorldChunk(serverWorld, protoChunk,
				chunkToAddEntitiesTo -> serverWorld.addEntities(EntityType.streamFromNbt(protoChunk.getEntities(), serverWorld))
		);
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
