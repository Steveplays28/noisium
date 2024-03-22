package io.github.steveplays28.noisium.mixin.server.world;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
	@Inject(method = "sendWatchPackets", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$cancelSendWatchPackets(ServerPlayerEntity player, ChunkPos pos, MutableObject<ChunkDataS2CPacket> packet, boolean oldWithinViewDistance, boolean newWithinViewDistance, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "shouldDelayShutdown", at = @At(value = "HEAD"), cancellable = true)
	private void noisium$cancelShutdownDelay(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}
}
