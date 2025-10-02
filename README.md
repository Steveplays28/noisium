![Noisium icon](https://raw.githubusercontent.com/coredex-source/noisium/24de632923c03864d65568a2cb5ce1e2cee0a304/docs/assets/icon/icon_128x128.png)

# Noisium Forked

This mod is a fork of the orignal and now unmaintained mod [Noisium](https://github.com/Steveplays28/noisium).
The fork intends to maintain compatibility with future Minecraft versions.

Optimises worldgen performance for a better gameplay experience.

Noisium changes some world generation functions that other mods don't touch, to fill in the gaps left by other performance optimisation
mods.
Most notably, `NoiseChunkGenerator#populateNoise` is optimised to speed up block state placement when generating new chunks.  
Setting the block state via abstractions/built-in functions is bypassed. Instead, the block states are set directly in the palette storage,
thus bypassing calculations Minecraft does that are normally useful when block states are set, but when generating the world only slow it
down.  
There are also 3 other optimisations, that increase biome population speed, block state sampling speed and chunk unlocking speed (Minecraft
1.21 and up) during world generation.

Noisium has full 1:1 parity with vanilla Minecraft world generation (world generation without Noisium).

The performance difference is variable, between a few seconds to a few dozen seconds faster depending on the amount of chunks generated.  
See the below Spark profiles for the differences in performance:

- [Vanilla](https://github.com/coredex-source/noisium/blob/1.21.9/docs/benchmarks/vanilla_minecraft_1_20_1.sparkprofile)
- [With Noisium](https://github.com/coredex-source/noisium/blob/1.21.9/docs/benchmarks/noisium_minecraft_1_20_1.sparkprofile)

## Dependencies

### Required

None. 

## Compatibility info

### Compatible mods

Noisium should be compatible with most, if not all, of the popular optimisation mods currently on Modrinth/CurseForge for
Noisium's supported Minecraft versions, since Noisium aims to fill in the gaps in performance optimisation left by other mods.
This includes (but is not limited to) C2ME, Lithium, Nvidium, and Sodium.

- C2ME: every world generation thread runs faster. The biome population multithreading is also done in a much better/more performant way in
  C2ME, so it's been removed from Noisium since `v1.0.2`. It's suggested to run C2ME alongside Noisium for even better world generation
  performance.
- Distant Horizons: Noisium speeds up LOD world generation threads, since LOD generation depends on Minecraft's world generation speed.
- ReTerraForged: RTF has built-in compatibility with Noisium, to fully utilize the optimisations during RTF world generation.

### Incompatibilities

See the [issue tracker](https://github.com/coredex-source/noisium/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+label%3Acompatibility) for
a list of incompatibilities.

See the version info in the filename for the supported Minecraft versions.  
Made for the Fabric, Quilt, and NeoForge modloaders.  
Server-side.

## FAQ

- Q: Will you be backporting this mod to lower Minecraft versions?  
  A: No.

- Q: Does this mod work in multiplayer?  
  A: Yes, but it'll only improve performance on the server.

- Q: Does only the server need this mod or does the client need it too?  
  A: Only the server needs this mod (but it works on the client too if you're going to host LAN or play singleplayer).

## Attribution

- Thank you to [Steveplays28](https://github.com/Steveplays28) for the orignal mod.
- Thank you to [Builderb0y](https://modrinth.com/user/Builderb0y) for giving great starting points and helping with issues
- Thank you to [ishland](https://github.com/ishland) for helping with C2ME compatibility and benchmarking performance
- Thank you to [Uniter](https://github.com/Uniter343) and [raccoonman2](https://github.com/racoonman2) for benchmarking performance

## License

This project is licensed under LGPLv3, see [LICENSE](https://github.com/coredex-source/noisium/blob/main/LICENSE).
