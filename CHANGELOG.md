# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## `v1.0.0` - 29/10/2023

### Added

- 4 worldgen performance optimisations
    - `ChainedBlockSource#sample` (replace an enhanced for loop with a `fori` loop for faster blockstate sampling)
    - `Chunk#populateBiomes` (multithread biome population)
    - `NoiseChunkGenerator#populateNoise` (set blockstates directly in the palette storage)
    - `NoiseChunkGenerator#populateNoise` (replace an enhanced for loop with a `fori` loop for faster chunk unlocking)
