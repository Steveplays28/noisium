package io.github.steveplays28.noisium.mixin.client.gui.hud;

import io.github.steveplays28.noisium.extension.world.server.NoisiumServerWorldExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(DebugHud.class)
public abstract class DebugHudMixin {
	@Shadow
	@Nullable
	protected abstract ServerWorld getServerWorld();

	@Shadow
	@Nullable
	private ChunkPos pos;

	@Inject(method = "getChunk", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$getChunkFromNoisiumServerWorldChunkManager(CallbackInfoReturnable<WorldChunk> cir) {
		var serverWorld = this.getServerWorld();
		var playerPosition = this.pos;
		if (serverWorld == null || playerPosition == null) {
			cir.setReturnValue(null);
			return;
		}

		cir.setReturnValue(((NoisiumServerWorldExtension) serverWorld).noisium$getServerWorldChunkManager().getChunk(
				new ChunkPos(playerPosition.x, playerPosition.z)));
	}
}
