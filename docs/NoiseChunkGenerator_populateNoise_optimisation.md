# NoiseChunkGenerator#populateNoise optimisations

This is what Noisium's code does, after the `@Redirect` mixin is applied. This provides a 20-30% speedup in `NoiseChunkGenerator#populateNoise`.
This is also a completely mapped version of `NoiseChunkGenerator#populateNoise` (including local variables), for future reference.

```java
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

    final int horizontalCellBlockCount = chunkNoiseSampler.getHorizontalCellBlockCount();
    final int verticalCellBlockCount = chunkNoiseSampler.getVerticalCellBlockCount();
    final int horizontalCellCount = 16 / horizontalCellBlockCount;
    final var mutableBlockPos = new BlockPos.Mutable();

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
                                    chunkSection.blockStateContainer.paletteProvider.computeIndex(chunkSectionBlockPosX,
                                            chunkSectionBlockPosY, chunkSectionBlockPosZ
                                    ), blockStateId);

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
```
