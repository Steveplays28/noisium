package io.github.steveplays28.noisium.mixin.compat.lithium;

import net.minecraft.block.BlockState;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NoiseChunkGenerator.class)
public abstract class LithiumNoiseChunkGeneratorMixin extends ChunkGenerator {
	@Shadow
	protected abstract Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight);

	public LithiumNoiseChunkGeneratorMixin(BiomeSource biomeSource) {
		super(biomeSource);
	}

	@Redirect(method = "populateNoise(Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/chunk/Chunk;II)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"))
	private BlockState noisium$populateNoiseWrapSetBlockStateOperation(@NotNull ChunkSection chunkSection, int chunkSectionBlockPosX, int chunkSectionBlockPosY, int chunkSectionBlockPosZ, @NotNull BlockState blockState, boolean lock) {
		// Set the blockstate directly using swapUnsafe for MC 1.21.9+ (similar to biome optimization)
		chunkSection.blockStateContainer.swapUnsafe(chunkSectionBlockPosX, chunkSectionBlockPosY, chunkSectionBlockPosZ, blockState);

		return blockState;
	}

	/**
	 * @author Steveplays28
	 * @reason Improved chunk locking and unlocking speed by getting the chunk section array directly from the chunk
	 * and by replacing {@code foreach} with {@code fori}.
	 */
	@Overwrite
	private @Nullable Chunk method_38332(@NotNull Chunk chunk, int generationShapeHeightFloorDiv, @NotNull GenerationShapeConfig generationShapeConfig, int minimumY, @NotNull Blender blender, @NotNull StructureAccessor structureAccessor, @NotNull NoiseConfig noiseConfig, int minimumYFloorDiv) {
		final int startingChunkSectionIndex = chunk.getSectionIndex(
				generationShapeHeightFloorDiv * generationShapeConfig.verticalCellBlockCount() - 1 + minimumY);
		final int minimumYChunkSectionIndex = chunk.getSectionIndex(minimumY);
		// Get the chunk section array from the chunk directly instead of constructing it manually
		@NotNull final var chunkSections = chunk.getSectionArray();
		for (int chunkSectionIndex = startingChunkSectionIndex; chunkSectionIndex >= minimumYChunkSectionIndex; --chunkSectionIndex) {
			chunkSections[chunkSectionIndex].lock();
		}

		@Nullable Chunk chunkWithNoise;
		try {
			chunkWithNoise = this.populateNoise(
					blender, structureAccessor, noiseConfig, chunk, minimumYFloorDiv, generationShapeHeightFloorDiv);
		} finally {
			// Replace an enhanced for loop with a fori loop and reuse the chunk sections array used when locking the chunk sections
			// Also run calculateCounts() on every chunk section to add Lithium compatibility
			for (int chunkSectionIndex = startingChunkSectionIndex; chunkSectionIndex >= minimumYChunkSectionIndex; --chunkSectionIndex) {
				@NotNull final var chunkSection = chunkSections[chunkSectionIndex];
				chunkSection.calculateCounts();
				chunkSection.unlock();
			}
		}

		return chunkWithNoise;
	}
}
