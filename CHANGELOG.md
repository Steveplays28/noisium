# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## `v1.0.2` - 12/11/2023

### Added

- C2ME recommendation
    - Running C2ME alongside Noisium is now recommended to replace the biome population multithreading, since C2ME does it in a much
      better/more performant way

### Fixed

- Potential race condition due to a non-thread safe `BlockPos.Mutable` instance

## `v1.0.1` - 05/11/2023

### Fixed

- Occasional missing chunk sections

## `v1.0.0` - 29/10/2023

### Added

- 4 worldgen performance optimisations
    - `ChainedBlockSource#sample` (replace an enhanced for loop with a `fori` loop for faster blockstate sampling)
    - `Chunk#populateBiomes` (multithread biome population)
    - `NoiseChunkGenerator#populateNoise` (set blockstates directly in the palette storage)
    - `NoiseChunkGenerator#populateNoise` (replace an enhanced for loop with a `fori` loop for faster chunk unlocking)
