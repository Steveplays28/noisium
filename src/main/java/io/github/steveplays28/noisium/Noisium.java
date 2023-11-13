package io.github.steveplays28.noisium;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Noisium implements ModInitializer {
	public static final String MOD_ID = "noisium";
	public static final String MOD_NAME = "Noisium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		LOGGER.info("Loading {}.", MOD_NAME);

		// Save/unload server instance on server start/stop
		ServerLifecycleEvents.SERVER_STARTING.register(server -> Noisium.server = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> Noisium.server = null);
	}
}
