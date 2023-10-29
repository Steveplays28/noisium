package io.github.steveplays28.noisium.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(NoiseChunkGenerator.class)
public abstract class NoiseChunkGeneratorMixin extends ChunkGenerator {
	@Shadow
	@Final
	public RegistryEntry<ChunkGeneratorSettings> settings;

	public NoiseChunkGeneratorMixin(BiomeSource biomeSource) {
		super(biomeSource);
	}

	// Attempt 1
//	@Inject(method = "populateNoise(Lnet/minecraft/world/gen/chunk/Blender;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/chunk/Chunk;II)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;"), locals = LocalCapture.CAPTURE_FAILSOFT)
//	private void populateNoiseInject(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight, CallbackInfoReturnable<Chunk> cir, ChunkNoiseSampler chunkNoiseSampler, Heightmap heightmap, Heightmap heightmap2, ChunkPos chunkPos, int i, int j, AquiferSampler aquiferSampler, BlockPos.Mutable mutable, int k, int l, int m, int n, int o, int p, int q, ChunkSection chunkSection, int r, int s, int t, int u, int v, double d, int w, int x, int y, double e, int z, int aa, int ab, double f, BlockState blockState) {
//		//noinspection SuspiciousNameCombination
//		chunkSection.blockStateContainer.set(y, u, ab, blockState);
//
//		//noinspection SuspiciousNameCombination
//		heightmap.trackUpdate(y, t, ab, blockState);
//		//noinspection SuspiciousNameCombination
//		heightmap2.trackUpdate(y, t, ab, blockState);
//
//		if (!aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) {
//			return;
//		}
//
//		mutable.set(x, t, aa);
//		chunk.markBlockForPostProcessing(mutable);
//
//		cir.cancel();
//	}

	// Attempt 2
//	/**
//	 * @author
//	 * @reason
//	 */
//	@Overwrite
//	private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight) {
//		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
//				chunk2 -> ((NoiseChunkGenerator) (Object) this).createChunkNoiseSampler(chunk2, structureAccessor, blender, noiseConfig));
//		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
//		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
//		ChunkPos chunkPos = chunk.getPos();
//		int i = chunkPos.getStartX();
//		int j = chunkPos.getStartZ();
//		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
//		chunkNoiseSampler.sampleStartDensity();
//		BlockPos.Mutable mutable = new BlockPos.Mutable();
//		int k = chunkNoiseSampler.getHorizontalCellBlockCount();
//		int l = chunkNoiseSampler.getVerticalCellBlockCount();
//		int m = 16 / k;
//		int n = 16 / k;
//
//		for (int o = 0; o < m; ++o) {
//			chunkNoiseSampler.sampleEndDensity(o);
//
//			for (int p = 0; p < n; ++p) {
//				int q = chunk.countVerticalSections() - 1;
//				ChunkSection chunkSection = chunk.getSection(q);
//				var paletteStorage = chunkSection.getBlockStateContainer().data.palette;
//
//				for (int r = cellHeight - 1; r >= 0; --r) {
//					chunkNoiseSampler.onSampledCellCorners(r, p);
//
//					for (int s = l - 1; s >= 0; --s) {
//						int blockPosY = (minimumCellY + r) * l + s;
//						int chunkSectionBlockPosY = blockPosY & 0xF;
//						int v = chunk.getSectionIndex(blockPosY);
//
//						if (q != v) {
//							q = v;
//							chunkSection = chunk.getSection(v);
//							paletteStorage = chunkSection.getBlockStateContainer().data.palette;
//						}
//
//						double d = (double) s / (double) l;
//						chunkNoiseSampler.interpolateY(blockPosY, d);
//
//						for (int w = 0; w < k; ++w) {
//							int blockPosX = i + o * k + w;
//							int chunkSectionBlockPosX = blockPosX & 0xF;
//							double e = (double) w / (double) k;
//							chunkNoiseSampler.interpolateX(blockPosX, e);
//
//							var airId = paletteStorage.index(Blocks.AIR.getDefaultState());
//							var waterId = paletteStorage.index(Blocks.WATER.getDefaultState());
//							var stoneId = paletteStorage.index(Blocks.STONE.getDefaultState());
//
//							// Handle palette storage resize
//							if (paletteStorage != (paletteStorage = chunkSection.getBlockStateContainer().data.palette)) {
//								airId = paletteStorage.index(Blocks.AIR.getDefaultState());
//								waterId = paletteStorage.index(Blocks.WATER.getDefaultState());
//								stoneId = paletteStorage.index(Blocks.STONE.getDefaultState());
//
//								assert paletteStorage == chunkSection.getBlockStateContainer().data.palette;
//							}
//
//							for (int z = 0; z < k; ++z) {
//								int blockPosZ = j + p * k + z;
//								int chunkSectionBlockPosZ = blockPosZ & 0xF;
//								double f = (double) z / (double) k;
//								chunkNoiseSampler.interpolateZ(blockPosZ, f);
//								BlockState blockState = chunkNoiseSampler.sampleBlockState();
//
//								if (blockState == null) {
//									blockState = ((NoiseChunkGenerator) (Object) this).settings.value().defaultBlock();
//								}
//
//								if (blockState == NoiseChunkGenerator.AIR || SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
//									continue;
//								}
//
//								if (blockState.isAir()) {
//									chunkSection.getBlockStateContainer().data.storage().set(
//											chunkSection.getBlockStateContainer().data.palette.index(blockState), airId);
//								} else if (blockState.equals(Blocks.WATER.getDefaultState())) {
//									chunkSection.getBlockStateContainer().data.storage().set(
//											chunkSection.getBlockStateContainer().data.palette.index(blockState), waterId);
//								}  else if (blockState.equals(Blocks.STONE.getDefaultState())) {
//									chunkSection.getBlockStateContainer().data.storage().set(
//											chunkSection.getBlockStateContainer().data.palette.index(blockState), stoneId);
//								} else {
//									chunkSection.setBlockState(
//											chunkSectionBlockPosX, chunkSectionBlockPosY, chunkSectionBlockPosZ, blockState, false);
//								}
//
//								heightmap.trackUpdate(chunkSectionBlockPosX, blockPosY, chunkSectionBlockPosZ, blockState);
//								heightmap2.trackUpdate(chunkSectionBlockPosX, blockPosY, chunkSectionBlockPosZ, blockState);
//
//								if (!aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) {
//									continue;
//								}
//
//								mutable.set(blockPosX, blockPosY, blockPosZ);
//								chunk.markBlockForPostProcessing(mutable);
//							}
//						}
//					}
//				}
//			}
//
//			chunkNoiseSampler.swapBuffers();
//		}
//
//		chunkNoiseSampler.stopInterpolation();
//		return chunk;
//	}

	// Original vanilla MC method cleaned up
//	/**
//	 * @author
//	 * @reason
//	 */
//	@Overwrite
//	private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight) {
//		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
//				chunk2 -> ((NoiseChunkGenerator) (Object) this).createChunkNoiseSampler(chunk2, structureAccessor, blender, noiseConfig));
//		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
//		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
//		ChunkPos chunkPos = chunk.getPos();
//		int chunkPosStartX = chunkPos.getStartX();
//		int chunkPosStartZ = chunkPos.getStartZ();
//		BlockPos.Mutable mutable = new BlockPos.Mutable();
//		int horizontalCellBlockCount = chunkNoiseSampler.getHorizontalCellBlockCount();
//		int verticalCellBlockCount = chunkNoiseSampler.getVerticalCellBlockCount();
//		int m = 16 / horizontalCellBlockCount;
//
//		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
//		chunkNoiseSampler.sampleStartDensity();
//
//		for (int o = 0; o < m; ++o) {
//			chunkNoiseSampler.sampleEndDensity(o);
//
//			for (int p = 0; p < m; ++p) {
//				int chunkVerticalSectionsMinusOne = chunk.countVerticalSections() - 1;
//				ChunkSection chunkSection = chunk.getSection(chunkVerticalSectionsMinusOne);
//
//				for (int r = cellHeight - 1; r >= 0; --r) {
//					chunkNoiseSampler.onSampledCellCorners(r, p);
//
//					for (int s = verticalCellBlockCount - 1; s >= 0; --s) {
//						int blockPosY = (minimumCellY + r) * verticalCellBlockCount + s;
//						int chunkSectionBlockPosY = blockPosY & 0xF;
//						int chunkSectionIndex = chunk.getSectionIndex(blockPosY);
//
//						if (chunkVerticalSectionsMinusOne != chunkSectionIndex) {
//							chunkVerticalSectionsMinusOne = chunkSectionIndex;
//							chunkSection = chunk.getSection(chunkSectionIndex);
//						}
//
//						double d = (double) s / verticalCellBlockCount;
//						chunkNoiseSampler.interpolateY(blockPosY, d);
//
//						for (int w = 0; w < horizontalCellBlockCount; ++w) {
//							int blockPosX = chunkPosStartX + o * horizontalCellBlockCount + w;
//							int chunkSectionBlockPosX = blockPosX & 0xF;
//							double deltaX = (double) w / horizontalCellBlockCount;
//							chunkNoiseSampler.interpolateX(blockPosX, deltaX);
//
//							for (int z = 0; z < horizontalCellBlockCount; ++z) {
//								int blockPosZ = chunkPosStartZ + p * horizontalCellBlockCount + z;
//								int chunkSectionBlockPosZ = blockPosZ & 0xF;
//								double deltaZ = (double) z / horizontalCellBlockCount;
//								chunkNoiseSampler.interpolateZ(blockPosZ, deltaZ);
//								BlockState blockState = chunkNoiseSampler.sampleBlockState();
//
//								if (blockState == null) {
//									blockState = ((NoiseChunkGenerator) (Object) this).settings.value().defaultBlock();
//								}
//
//								if (blockState == NoiseChunkGenerator.AIR || SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
//									continue;
//								}
//
//								chunkSection.setBlockState(
//										chunkSectionBlockPosX, chunkSectionBlockPosY, chunkSectionBlockPosZ, blockState, false);
//
//								heightmap.trackUpdate(chunkSectionBlockPosX, blockPosY, chunkSectionBlockPosZ, blockState);
//								heightmap2.trackUpdate(chunkSectionBlockPosX, blockPosY, chunkSectionBlockPosZ, blockState);
//
//								if (!aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) {
//									continue;
//								}
//
//								mutable.set(blockPosX, blockPosY, blockPosZ);
//								chunk.markBlockForPostProcessing(mutable);
//							}
//						}
//					}
//				}
//			}
//
//			chunkNoiseSampler.swapBuffers();
//		}
//
//		chunkNoiseSampler.stopInterpolation();
//		return chunk;
//	}

	/**
	 * @author Steveplays28
	 * @reason TODO
	 */
	@Overwrite
	private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight) {
		final ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
				chunk2 -> ((NoiseChunkGenerator) (Object) this).createChunkNoiseSampler(chunk2, structureAccessor, blender, noiseConfig));
		final Heightmap oceanFloorHeightMap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		final Heightmap worldSurfaceHeightMap = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		final ChunkPos chunkPos = chunk.getPos();
		final int chunkPosStartX = chunkPos.getStartX();
		final int chunkPosStartZ = chunkPos.getStartZ();
		final var aquiferSampler = chunkNoiseSampler.getAquiferSampler();

		chunkNoiseSampler.sampleStartDensity();

		final var mutableBlockPos = new BlockPos.Mutable();
		final int horizontalCellBlockCount = chunkNoiseSampler.getHorizontalCellBlockCount();
		final int verticalCellBlockCount = chunkNoiseSampler.getVerticalCellBlockCount();
		final int horizontalCellCount = 16 / horizontalCellBlockCount;

		for (int baseHorizontalWidthCellIndex = 0; baseHorizontalWidthCellIndex < horizontalCellCount; ++baseHorizontalWidthCellIndex) {
			chunkNoiseSampler.sampleEndDensity(baseHorizontalWidthCellIndex);

			for (int baseHorizontalLengthCellIndex = 0; baseHorizontalLengthCellIndex < horizontalCellCount; ++baseHorizontalLengthCellIndex) {
				var nextChunkSectionIndex = chunk.countVerticalSections() - 1;
				var chunkSection = chunk.getSection(nextChunkSectionIndex);

				for (int verticalCellHeightIndex = cellHeight - 1; verticalCellHeightIndex >= 0; --verticalCellHeightIndex) {
					chunkNoiseSampler.onSampledCellCorners(verticalCellHeightIndex, baseHorizontalLengthCellIndex);

					for (int verticalCellBlockIndex = verticalCellBlockCount - 1; verticalCellBlockIndex >= 0; --verticalCellBlockIndex) {
						int blockPosY = (minimumCellY + verticalCellHeightIndex) * verticalCellBlockCount + verticalCellBlockIndex;
						int chunkSectionBlockPosY = blockPosY & 0xF;
						int chunkSectionIndex = chunk.getSectionIndex(blockPosY);
						boolean isFirstLoopInTheSection = true;

						if (nextChunkSectionIndex != chunkSectionIndex) {
							nextChunkSectionIndex = chunkSectionIndex;
							chunkSection = chunk.getSection(chunkSectionIndex);
						}

						double deltaY = (double) verticalCellBlockIndex / verticalCellBlockCount;
						chunkNoiseSampler.interpolateY(blockPosY, deltaY);

						for (int horizontalWidthCellBlockIndex = 0; horizontalWidthCellBlockIndex < horizontalCellBlockCount; ++horizontalWidthCellBlockIndex) {
							int blockPosX = chunkPosStartX + baseHorizontalWidthCellIndex * horizontalCellBlockCount + horizontalWidthCellBlockIndex;
							int chunkSectionBlockPosX = blockPosX & 0xF;
							double deltaX = (double) horizontalWidthCellBlockIndex / horizontalCellBlockCount;

							chunkNoiseSampler.interpolateX(blockPosX, deltaX);

							for (int horizontalLengthCellBlockIndex = 0; horizontalLengthCellBlockIndex < horizontalCellBlockCount; ++horizontalLengthCellBlockIndex) {
								int blockPosZ = chunkPosStartZ + baseHorizontalLengthCellIndex * horizontalCellBlockCount + horizontalLengthCellBlockIndex;
								int chunkSectionBlockPosZ = blockPosZ & 0xF;
								double deltaZ = (double) horizontalLengthCellBlockIndex / horizontalCellBlockCount;

								chunkNoiseSampler.interpolateZ(blockPosZ, deltaZ);
								BlockState blockState = chunkNoiseSampler.sampleBlockState();

								if (blockState == null) {
									blockState = ((NoiseChunkGenerator) (Object) this).settings.value().defaultBlock();
								}

								if (blockState == NoiseChunkGenerator.AIR || SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
									continue;
								}

								// TODO: Replace this bandaid fix with an actual fix
								if (isFirstLoopInTheSection) {
									chunkSection.setBlockState(
											chunkSectionBlockPosX, chunkSectionBlockPosY, chunkSectionBlockPosZ, blockState, false);
									isFirstLoopInTheSection = false;
								}

								// Set the blockstate in the palette storage directly to improve performance
								chunkSection.blockStateContainer.data.storage().set(
										chunkSection.blockStateContainer.paletteProvider.computeIndex(chunkSectionBlockPosX,
												chunkSectionBlockPosY, chunkSectionBlockPosZ
										), chunkSection.blockStateContainer.data.palette.index(blockState));

								// Update the lighting on the client after setting the block state directly
								// This avoids issues with MC's lighting engine not recognising the direct palette storage blockstate update
								if (chunk instanceof WorldChunk worldChunk) {
									final var world = worldChunk.getWorld();

									if (world.isClient()) {
										mutableBlockPos.set(chunkSectionBlockPosX, chunkSectionBlockPosY, chunkSectionBlockPosZ);
										world.getChunkManager().getLightingProvider().checkBlock(mutableBlockPos);
									}
								}

								oceanFloorHeightMap.trackUpdate(chunkSectionBlockPosX, blockPosY, chunkSectionBlockPosZ, blockState);
								worldSurfaceHeightMap.trackUpdate(chunkSectionBlockPosX, blockPosY, chunkSectionBlockPosZ, blockState);

								if (!aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) {
									continue;
								}

								mutableBlockPos.set(blockPosX, blockPosY, blockPosZ);
								chunk.markBlockForPostProcessing(mutableBlockPos);
							}
						}
					}
				}
			}

			chunkNoiseSampler.swapBuffers();
		}

		chunkNoiseSampler.stopInterpolation();
		return chunk;
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
		int j = MathHelper.floorDiv(minimumY, generationShapeConfig.verticalCellBlockCount());
		int k = MathHelper.floorDiv(generationShapeConfig.height(), generationShapeConfig.verticalCellBlockCount());

		if (k <= 0) {
			return CompletableFuture.completedFuture(chunk);
		}

		int l = chunk.getSectionIndex(k * generationShapeConfig.verticalCellBlockCount() - 1 + minimumY);
		int minimumYChunkSectionIndex = chunk.getSectionIndex(minimumY);
		ArrayList<ChunkSection> set = new ArrayList<>();

		for (int n = l; n >= minimumYChunkSectionIndex; --n) {
			ChunkSection chunkSection = chunk.getSection(n);

			chunkSection.lock();
			set.add(chunkSection);
		}

		return CompletableFuture.supplyAsync(
				Util.debugSupplier("wgen_fill_noise", () -> this.populateNoise(blender, structureAccessor, noiseConfig, chunk, j, k)),
				Util.getMainWorkerExecutor()
		).whenCompleteAsync((chunk2, throwable) -> {
			// Replace an enhanced for loop with a fori loop
			for (int i = 0; i < set.size(); i++) {
				set.get(i).unlock();
			}
		}, executor);
	}
}
