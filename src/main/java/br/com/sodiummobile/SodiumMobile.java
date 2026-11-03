package br.com.sodiummobile;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
    modid = SodiumMobile.MODID,
    name = SodiumMobile.MOD_NAME,
    version = SodiumMobile.VERSION,
    acceptedMinecraftVersions = "[1.8.9]"
)
public class SodiumMobile {
    public static final String MODID = "sodiummobile";
    public static final String MOD_NAME = "Sodium Mobile";
    public static final String VERSION = "0.1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[Sodium Mobile] Mod iniciado para Minecraft 1.8.9.");
    }
}
