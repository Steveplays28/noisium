package io.github.steveplays28.noisium.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(NoiseChunkGenerator.class)
public abstract class NoiseChunkGeneratorMixin extends ChunkGenerator {
	@Shadow
	@Final
	public RegistryEntry<ChunkGeneratorSettings> settings;

	@Shadow
	protected abstract Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight);

	public NoiseChunkGeneratorMixin(BiomeSource biomeSource) {
		super(biomeSource);
	}

	@Redirect(method = "populateNoise(Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/chunk/Chunk;II)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"))
	private BlockState noisium$populateNoiseWrapSetBlockStateOperation(ChunkSection chunkSection, int chunkSectionBlockPosX, int chunkSectionBlockPosY, int chunkSectionBlockPosZ, BlockState blockState, boolean lock) {
		// Update the non empty block count to avoid issues with MC's lighting engine and other systems not recognising the direct palette storage set
		// See ChunkSection#setBlockState
		chunkSection.nonEmptyBlockCount += 1;

		if (!blockState.getFluidState().isEmpty()) {
			chunkSection.nonEmptyFluidCount += 1;
		}

		if (blockState.hasRandomTicks()) {
			chunkSection.randomTickableBlockCount += 1;
		}

		// Set the blockstate in the palette storage directly to improve performance
		var blockStateId = chunkSection.blockStateContainer.data.palette.index(blockState);
		chunkSection.blockStateContainer.data.storage().set(
				chunkSection.blockStateContainer.paletteProvider.computeIndex(chunkSectionBlockPosX, chunkSectionBlockPosY,
						chunkSectionBlockPosZ
				), blockStateId);

		return blockState;
	}

	/**
	 * @author Steveplays28
	 * @reason Micro-optimisation
	 */
	@Overwrite
	@SuppressWarnings("ForLoopReplaceableByForEach")
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
		GenerationShapeConfig generationShapeConfig = this.settings.value().generationShapeConfig().trimHeight(chunk.getHeightLimitView());
		int minimumY = generationShapeConfig.minimumY();
		int generationShapeHeightFloorDiv = Math.floorDiv(generationShapeConfig.height(), generationShapeConfig.verticalCellBlockCount());

		if (generationShapeHeightFloorDiv <= 0) {
			return CompletableFuture.completedFuture(chunk);
		}

		int minimumYFloorDiv = Math.floorDiv(minimumY, generationShapeConfig.verticalCellBlockCount());
		int startingChunkSectionIndex = chunk.getSectionIndex(
				generationShapeHeightFloorDiv * generationShapeConfig.verticalCellBlockCount() - 1 + minimumY);
		int minimumYChunkSectionIndex = chunk.getSectionIndex(minimumY);
		ArrayList<ChunkSection> chunkSections = new ArrayList<>();

		for (int chunkSectionIndex = startingChunkSectionIndex; chunkSectionIndex >= minimumYChunkSectionIndex; --chunkSectionIndex) {
			ChunkSection chunkSection = chunk.getSection(chunkSectionIndex);

			chunkSection.lock();
			chunkSections.add(chunkSection);
		}

		return CompletableFuture.supplyAsync(
				Util.debugSupplier("wgen_fill_noise",
						() -> this.populateNoise(blender, structureAccessor, noiseConfig, chunk, minimumYFloorDiv,
								generationShapeHeightFloorDiv
						)
				), Util.getMainWorkerExecutor()).whenCompleteAsync((chunk2, throwable) -> {
			// Replace an enhanced for loop with a fori loop
			for (int i = 0; i < chunkSections.size(); i++) {
				chunkSections.get(i).unlock();
			}
		}, executor);
	}
}
