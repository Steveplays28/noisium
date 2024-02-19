package io.github.steveplays28.noisium.mixin;

import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Caches the horizontalCellBlockCount and verticalCellBlockCount, so it doesn't have to convert from biome coordinates to block coordinates every time.
 */
@Mixin(GenerationShapeConfig.class)
public abstract class GenerationShapeConfigMixin {
	@Unique
	private int noisium$horizontalCellBlockCount;
	@Unique
	private int noisium$verticalCellBlockCount;

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void noisium$createCacheHorizontalAndVerticalCellBlockCountInject(int minimumY, int height, int horizontalSize, int verticalSize, CallbackInfo ci) {
		noisium$horizontalCellBlockCount = BiomeCoords.toBlock(horizontalSize);
		noisium$verticalCellBlockCount = BiomeCoords.toBlock(verticalSize);
	}

	@Inject(method = "horizontalCellBlockCount", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$horizontalCellBlockCountGetFromCacheInject(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(noisium$horizontalCellBlockCount);
	}

	@Inject(method = "verticalCellBlockCount", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$verticalCellBlockCountGetFromCacheInject(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(noisium$verticalCellBlockCount);
	}
}
