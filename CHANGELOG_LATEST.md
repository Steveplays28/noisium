### Added

- C2ME recommendation
    - Running C2ME alongside Noisium is now recommended to replace the biome population multithreading, since C2ME does it in a much
      better/more performant way

### Changed

- Removed the biome population multithreading
    - See the C2ME recommendation above

### Fixed

- Potential race condition due to a non-thread safe `BlockPos.Mutable` instance
