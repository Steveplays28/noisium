package io.github.steveplays28.noisium.neoforge;

import io.github.steveplays28.noisium.Noisium;
import net.neoforged.fml.common.Mod;

@Mod(Noisium.MOD_ID)
public class NoisiumNeoForge {
    public NoisiumNeoForge() {
        Noisium.initialize();
    }
}
