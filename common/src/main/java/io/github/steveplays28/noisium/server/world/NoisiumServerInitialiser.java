package io.github.steveplays28.noisium.server.world;

import dev.architectury.event.events.common.LifecycleEvent;
import io.github.steveplays28.noisium.server.player.NoisiumServerPlayerChunkLoader;

public class NoisiumServerInitialiser {
	/**
	 * Keeps a reference to the {@link NoisiumServerPlayerChunkLoader}, to make sure it doesn't get garbage collected until the object is no longer necessary.
	 */
	@SuppressWarnings("unused")
	private static NoisiumServerPlayerChunkLoader serverPlayerChunkLoader;

	public static void initialise() {
		LifecycleEvent.SERVER_STARTED.register(instance -> serverPlayerChunkLoader = new NoisiumServerPlayerChunkLoader());
		LifecycleEvent.SERVER_STOPPING.register(instance -> serverPlayerChunkLoader = null);
	}
}
