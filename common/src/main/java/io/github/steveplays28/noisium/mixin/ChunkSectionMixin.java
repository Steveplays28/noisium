package io.github.steveplays28.noisium.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.ReadableContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkSection.class)
public class ChunkSectionMixin {
	@Unique
	private static final int sliceSize = 4;

	@Shadow
	private ReadableContainer<RegistryEntry<Biome>> biomeContainer;

	/**
	 * @author Steveplays28
	 * @reason Axis order micro-optimisation
	 */
	@Overwrite
	public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler, int x, int y, int z) {
		PalettedContainer<RegistryEntry<Biome>> palettedContainer = this.biomeContainer.slice();

		for (int posX = 0; posX < sliceSize; ++posX) {
			for (int posZ = 0; posZ < sliceSize; ++posZ) {
				for (int posY = 0; posY < sliceSize; ++posY) {
					palettedContainer.swapUnsafe(posX, posY, posZ, biomeSupplier.getBiome(x + posX, y + posY, z + posZ, sampler));
				}
			}
		}

		this.biomeContainer = palettedContainer;
	}
}
