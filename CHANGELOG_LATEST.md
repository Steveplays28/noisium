### Added

- Forge support
- NeoForge support
- `GenerationShapeConfig` caching optimisation
    - `horizontalCellBlockCount` and `verticalCellBlockCount` are now cached, which skips a `BiomeCoords#toBlock` call every time these
      methods are invoked
