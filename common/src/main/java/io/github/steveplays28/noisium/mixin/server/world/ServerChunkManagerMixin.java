package io.github.steveplays28.noisium.mixin.server.world;

import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

// FIXME: Remove this mixin once the server chunk manager is fully replaced
@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
	@Inject(method = "executeQueuedTasks", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$stopServerChunkManagerFromRunningTasks(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;Z)V", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$stopServerChunkManagerFromTicking(BooleanSupplier shouldKeepTicking, boolean tickChunks, CallbackInfo ci) {
		ci.cancel();
	}
}
