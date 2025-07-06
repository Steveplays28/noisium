package io.github.steveplays28.noisium.mixin.compat.lithium;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

// TODO: Re-add compatibility for Lithium for Minecraft >=1.21 in a separate source set
//  Also move this class into a separate Minceraft <1.21 source set
@Mixin(NoiseBasedChunkGenerator.class)
public abstract class LithiumNoiseBasedChunkGeneratorMixin extends ChunkGenerator {
	public LithiumNoiseBasedChunkGeneratorMixin(BiomeSource biomeSource) {
		super(biomeSource);
	}

	@Redirect(
			method = "doFill(Lnet/minecraft/world/level/levelgen/blending/Blender;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;II)Lnet/minecraft/world/level/chunk/ChunkAccess;",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState noisium$optimiseSetBlockState(@NotNull LevelChunkSection chunkSection, int chunkSectionBlockPosX, int chunkSectionBlockPosY, int chunkSectionBlockPosZ,
			@NotNull BlockState blockState, boolean lock) {
		// Set the blockstate in the palette storage directly to improve performance
		var blockStateId = chunkSection.states.data.palette.idFor(blockState);
		chunkSection.states.data.storage().set(chunkSection.states.strategy.getIndex(chunkSectionBlockPosX, chunkSectionBlockPosY, chunkSectionBlockPosZ), blockStateId);

		return blockState;
	}

	@Inject(method = "/lambda\\$fillFromNoise\\$\\d+/", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;release()V"))
	private static void noisium$recalculateBlockCounts(Set<LevelChunkSection> chunkSections, @NotNull ChunkAccess chunk, @Nullable Throwable throwable, @NotNull CallbackInfo ci,
			@Local @NotNull LevelChunkSection chunkSection) {
		// Calculate the block state counts on every chunk section to add Lithium compatibility
		// TODO: Use Lithium's new interface to count blocks in noisium$optimiseSetBlockState
		chunkSection.recalcBlockCounts();
	}
}
