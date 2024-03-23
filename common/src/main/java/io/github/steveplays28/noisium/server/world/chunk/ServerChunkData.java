package io.github.steveplays28.noisium.server.world.chunk;

import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;

public record ServerChunkData(@Nullable WorldChunk worldChunk, @NotNull Short blockUpdates, @NotNull BitSet blockLightBits,
                              @NotNull BitSet skyLightBits) {}
