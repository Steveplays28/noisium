package io.github.steveplays28.noisium.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.stream.IntStream;

@Mixin(Chunk.class)
public abstract class ChunkMixin {
	@Shadow
	public abstract ChunkPos getPos();

	@Shadow
	public abstract HeightLimitView getHeightLimitView();

	@Shadow
	public abstract ChunkSection getSection(int yIndex);

	/**
	 * @author Steveplays28
	 * @reason Multithreading optimisation
	 */
	@Overwrite
	public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler) {
		var chunkPos = this.getPos();
		int biomeCoordStartX = BiomeCoords.fromBlock(chunkPos.getStartX());
		int biomeCoordStartZ = BiomeCoords.fromBlock(chunkPos.getStartZ());
		var heightLimitView = this.getHeightLimitView();

		// Populate every section's biomes using multithreading
		IntStream.range(heightLimitView.getBottomSectionCoord(), heightLimitView.getTopSectionCoord()).parallel().forEach(
				sectionCoord -> this.getSection(heightLimitView.sectionCoordToIndex(sectionCoord)).populateBiomes(biomeSupplier, sampler,
						biomeCoordStartX, BiomeCoords.fromChunk(sectionCoord), biomeCoordStartZ
				));
	}
}
