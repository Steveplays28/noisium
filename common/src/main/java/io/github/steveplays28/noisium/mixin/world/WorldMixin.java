package io.github.steveplays28.noisium.mixin.world;

import io.github.steveplays28.noisium.extension.world.server.NoisiumServerWorldExtension;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {
	@Shadow
	public abstract boolean isClient();

	@Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$getChunkFromNoisiumServerChunkManager(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create, CallbackInfoReturnable<Chunk> cir) {
		if (this.isClient()) {
			return;
		}

		cir.setReturnValue(
				((NoisiumServerWorldExtension) this).noisium$getServerWorldChunkManager().getChunk(new ChunkPos(chunkX, chunkZ)));
	}
}
