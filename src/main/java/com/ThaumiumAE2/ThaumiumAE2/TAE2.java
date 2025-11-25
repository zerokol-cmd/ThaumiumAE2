package com.ThaumiumAE2.ThaumiumAE2;

import appeng.api.storage.StorageChannel;
import com.ThaumiumAE2.ThaumiumAE2.Proxies.CommonProxy;
import com.ThaumiumAE2.ThaumiumAE2.Registers.GUIFactoriesRegister;
import com.ThaumiumAE2.ThaumiumAE2.Registers.GridCacheRegister;
import com.ThaumiumAE2.ThaumiumAE2.Registers.ItemRegister;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = TAE2.MODID, version = Tags.VERSION, name = "Thaumium AE2", acceptedMinecraftVersions = "[1.7.10]")
public class TAE2 {

    public static final String MODID = "ThaumiumAE2";

//    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.ThaumiumAE2.ThaumiumAE2.Proxies.ClientProxy", serverSide = "com.ThaumiumAE2.ThaumiumAE2.Proxies.CommonProxy")
    public static CommonProxy proxy;
    public static StorageChannel ESSENTIA_STORAGE = StorageChannel.valueOf("ESSENTIA");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        ItemRegister.register();
        GUIFactoriesRegister.register();
        GridCacheRegister.register();
//        LOG.info("ESSENTIA_STORAGE = {}", ESSENTIA_STORAGE);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
