package io.github.steveplays28.noisium;

import io.github.steveplays28.noisium.server.world.NoisiumServerInitialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Noisium {
	public static final String MOD_ID = "noisium";
	public static final String MOD_NAME = "Noisium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void initialize() {
		LOGGER.info("Loading {}.", MOD_NAME);

		NoisiumServerInitialiser.initialise();
	}
}
