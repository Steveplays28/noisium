package io.github.steveplays28.noisium;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Noisium implements ModInitializer {
	public static final String MOD_ID = "noisium";
	public static final String MOD_NAME = "Noisium";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loading {}.", MOD_NAME);
	}
}
