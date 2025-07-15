package io.github.steveplays28.noisium.util;

import net.minecraftforge.fml.loading.LoadingModList;
import net.msrandom.multiplatform.annotations.Actual;

@Actual
public class ModUtilActual {
	/**
	 * Checks if a mod is present during loading.
	 */
	@Actual
	public static boolean isModPresent(String id) {
		return LoadingModList.get().getModFileById(id) != null;
	}
}
