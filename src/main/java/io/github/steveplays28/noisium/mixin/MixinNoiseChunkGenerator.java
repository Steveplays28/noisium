package io.github.steveplays28.noisium.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;

@Mixin(NoiseChunkGenerator.class)
public abstract class MixinNoiseChunkGenerator extends ChunkGenerator {
	public MixinNoiseChunkGenerator(BiomeSource biomeSource) {
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
	 * @author
	 * @reason
	 */
	@Overwrite
	private Chunk populateNoise(Blender blender, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk, int minimumCellY, int cellHeight) {
		ChunkNoiseSampler chunkNoiseSampler = chunk.getOrCreateChunkNoiseSampler(
				chunk2 -> ((NoiseChunkGenerator) (Object) this).createChunkNoiseSampler(chunk2, structureAccessor, blender, noiseConfig));
		Heightmap heightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap heightmap2 = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		ChunkPos chunkPos = chunk.getPos();
		int chunkPosStartX = chunkPos.getStartX();
		int chunkPosStartZ = chunkPos.getStartZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int horizontalCellBlockCount = chunkNoiseSampler.getHorizontalCellBlockCount();
		int verticalCellBlockCount = chunkNoiseSampler.getVerticalCellBlockCount();
		int m = 16 / horizontalCellBlockCount;

		AquiferSampler aquiferSampler = chunkNoiseSampler.getAquiferSampler();
		chunkNoiseSampler.sampleStartDensity();

		for (int o = 0; o < m; ++o) {
			chunkNoiseSampler.sampleEndDensity(o);

			for (int p = 0; p < m; ++p) {
				int chunkVerticalSectionsMinusOne = chunk.countVerticalSections() - 1;
				ChunkSection chunkSection = chunk.getSection(chunkVerticalSectionsMinusOne);

				for (int r = cellHeight - 1; r >= 0; --r) {
					chunkNoiseSampler.onSampledCellCorners(r, p);

					for (int s = verticalCellBlockCount - 1; s >= 0; --s) {
						int blockPosY = (minimumCellY + r) * verticalCellBlockCount + s;
						int chunkSectionBlockPosY = blockPosY & 0xF;
						int chunkSectionIndex = chunk.getSectionIndex(blockPosY);

						if (chunkVerticalSectionsMinusOne != chunkSectionIndex) {
							chunkVerticalSectionsMinusOne = chunkSectionIndex;
							chunkSection = chunk.getSection(chunkSectionIndex);
						}

						double d = (double) s / verticalCellBlockCount;
						chunkNoiseSampler.interpolateY(blockPosY, d);

						for (int w = 0; w < horizontalCellBlockCount; ++w) {
							int blockPosX = chunkPosStartX + o * horizontalCellBlockCount + w;
							int chunkSectionBlockPosX = blockPosX & 0xF;
							double deltaX = (double) w / horizontalCellBlockCount;
							chunkNoiseSampler.interpolateX(blockPosX, deltaX);

							for (int z = 0; z < horizontalCellBlockCount; ++z) {
								int blockPosZ = chunkPosStartZ + p * horizontalCellBlockCount + z;
								int chunkSectionBlockPosZ = blockPosZ & 0xF;
								double deltaZ = (double) z / horizontalCellBlockCount;
								chunkNoiseSampler.interpolateZ(blockPosZ, deltaZ);
								BlockState blockState = chunkNoiseSampler.sampleBlockState();

								if (blockState == null) {
									blockState = ((NoiseChunkGenerator) (Object) this).settings.value().defaultBlock();
								}

								if (blockState == NoiseChunkGenerator.AIR || SharedConstants.isOutsideGenerationArea(chunk.getPos())) {
									continue;
								}

								chunkSection.setBlockState(
										chunkSectionBlockPosX, chunkSectionBlockPosY, chunkSectionBlockPosZ, blockState, false);

								heightmap.trackUpdate(chunkSectionBlockPosX, blockPosY, chunkSectionBlockPosZ, blockState);
								heightmap2.trackUpdate(chunkSectionBlockPosX, blockPosY, chunkSectionBlockPosZ, blockState);

								if (!aquiferSampler.needsFluidTick() || blockState.getFluidState().isEmpty()) {
									continue;
								}

								mutable.set(blockPosX, blockPosY, blockPosZ);
								chunk.markBlockForPostProcessing(mutable);
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
	 * @author
	 * @reason
	 */
	@Overwrite
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk2) {

	}
}