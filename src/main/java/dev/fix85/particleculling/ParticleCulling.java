package dev.fix85.particleculling;

import net.fabricmc.api.ClientModInitializer;

public class ParticleCulling implements ClientModInitializer {
    public static final String MOD_ID = "particleculling";

    @Override
    public void onInitializeClient() {
        Config.load();
    }
}
