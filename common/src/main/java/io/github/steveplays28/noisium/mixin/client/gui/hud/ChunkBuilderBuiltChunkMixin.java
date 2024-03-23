package io.github.steveplays28.noisium.mixin.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ChunkBuilder.BuiltChunk.class)
public class ChunkBuilderBuiltChunkMixin {
	@Inject(method = "cancelRebuild", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$cancelRebuildDebug(CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "shouldBuild", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$shouldAlwaysBuildDebug(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
