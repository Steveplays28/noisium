package io.github.steveplays28.noisium.mixin.server.world;

import io.github.steveplays28.noisium.extension.world.server.NoisiumServerWorldExtension;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

// FIXME: Remove this mixin once the server chunk manager is fully replaced
@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {
	@Shadow
	public abstract World getWorld();

	@Inject(method = "executeQueuedTasks", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$stopServerChunkManagerFromRunningTasks(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}

	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;Z)V", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$stopServerChunkManagerFromTicking(BooleanSupplier shouldKeepTicking, boolean tickChunks, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "getWorldChunk", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$getWorldChunkFromNoisiumServerWorldChunkManager(int chunkX, int chunkZ, CallbackInfoReturnable<WorldChunk> cir) {
		cir.setReturnValue(((NoisiumServerWorldExtension) this.getWorld()).noisium$getServerWorldChunkManager().getChunk(new ChunkPos(chunkX, chunkZ)));
	}
}
