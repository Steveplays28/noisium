package io.github.steveplays28.noisium.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.ChainedBlockSource;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ChainedBlockSource.class)
public abstract class ChainedBlockSourceMixin {
	@Shadow
	@Final
	private List<ChunkNoiseSampler.BlockStateSampler> samplers;

	/**
	 * @author Steveplays28
	 * @reason Micro-optimisation
	 */
	@Overwrite
	@Nullable
	@SuppressWarnings("ForLoopReplaceableByForEach")
	public BlockState sample(DensityFunction.NoisePos pos) {
		for (int i = 0; i < this.samplers.size(); i++) {
			BlockState blockState = this.samplers.get(i).sample(pos);
			if (blockState == null) {
				continue;
			}

			return blockState;
		}

		return null;
	}
}
