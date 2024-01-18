package io.github.steveplays28.noisium.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.seibel.distanthorizons.api.DhApi;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChunkNoiseSampler.class)
public class ChunkNoiseSamplerMixin {
	@Shadow
	private int startBlockX;

	@Shadow
	int cellBlockX;

	@Shadow
	@Final
	private int startCellZ;

	@Shadow
	@Final
	int horizontalCellCount;

	@Shadow
	private int startBlockZ;

	@Shadow
	int cellBlockZ;

	@Shadow
	@Final
	int horizontalCellBlockCount;

	@Shadow
	long cacheOnceUniqueIndex;

	@Shadow
	@Final
	private DensityFunction.EachApplier interpolationEachApplier;

	@Shadow
	@Final
	List<ChunkNoiseSampler.DensityInterpolator> interpolators;

	@Shadow
	int index;

	@Shadow
	int cellBlockY;

	@Shadow
	@Final
	int verticalCellBlockCount;

	/**
	 * @author Steveplays28
	 * @reason Optimise density sampling for Distant Horizons' LODs
	 */
	@Overwrite
	private void sampleDensity(boolean start, int cellX) {
		// TODO: Replace the constants 4 and 16 with variables for chunk size and DhLodHorizontalCellBlockCount
		int horizontalCellBlockCount = DhApi.isDhThread() ? 4 : this.horizontalCellBlockCount;
		int horizontalCellCount = DhApi.isDhThread() ? 16 / horizontalCellBlockCount : this.horizontalCellCount;
		this.startBlockX = cellX * horizontalCellBlockCount;
		this.cellBlockX = 0;

		for (int i = 0; i < horizontalCellCount + 1; ++i) {
			int j = this.startCellZ + i;
			this.startBlockZ = j * horizontalCellBlockCount;
			this.cellBlockZ = 0;
			++this.cacheOnceUniqueIndex;

			for (ChunkNoiseSampler.DensityInterpolator densityInterpolator : this.interpolators) {
				double[] ds = (start ? densityInterpolator.startDensityBuffer : densityInterpolator.endDensityBuffer)[i];
				densityInterpolator.fill(ds, this.interpolationEachApplier);
			}
		}

		++this.cacheOnceUniqueIndex;
	}

	/**
	 * @author Steveplays28
	 * @reason Axis order micro-optimisation
	 */
	@Overwrite
	public void fill(double[] densities, DensityFunction densityFunction) {
		this.index = 0;

		for (int horizontalWidthCellBlock = 0; horizontalWidthCellBlock < this.horizontalCellBlockCount; horizontalWidthCellBlock++) {
			this.cellBlockX = horizontalWidthCellBlock;

			for (int horizontalLengthCellBlock = 0; horizontalLengthCellBlock < this.horizontalCellBlockCount; horizontalLengthCellBlock++) {
				this.cellBlockZ = horizontalLengthCellBlock;

				for (int verticalCellBlock = this.verticalCellBlockCount - 1; verticalCellBlock >= 0; --verticalCellBlock) {
					this.cellBlockY = verticalCellBlock;

					densities[this.index++] = densityFunction.sample((DensityFunction.NoisePos) this);
				}
			}
		}
	}

	@Mixin(ChunkNoiseSampler.DensityInterpolator.class)
	public static class DensityInterpolatorMixin {
		@Shadow
		@Final
		ChunkNoiseSampler field_34622;

		@Inject(method = "createBuffer", at = @At(value = "HEAD"))
		private void noisium$createBufferModifyArgs(int sizeZ, int sizeX, CallbackInfoReturnable<double[][]> cir, @Local(ordinal = 1) @NotNull LocalIntRef sizeXLocal) {
			sizeXLocal.set(DhApi.isDhThread() ? 16 / 4 : field_34622.getHorizontalCellBlockCount());
		}
	}
}
