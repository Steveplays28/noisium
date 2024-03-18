package io.github.steveplays28.noisium.server.world;

import com.mojang.datafixers.DataFixer;
import net.minecraft.world.storage.VersionedChunkStorage;

import java.nio.file.Path;

public class NoisiumServerVersionedChunkStorage extends VersionedChunkStorage {
	public NoisiumServerVersionedChunkStorage(Path directory, DataFixer dataFixer, boolean dsync) {
		super(directory, dataFixer, dsync);
	}
}
