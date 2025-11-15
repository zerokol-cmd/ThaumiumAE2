package com.ThaumiumAE2.ThaumiumAE2;

import appeng.api.storage.StorageChannel;
import appeng.core.CreativeTab;
import com.ThaumiumAE2.ThaumiumAE2.Items.ItemThaumiumCell;
import com.ThaumiumAE2.api.ITAE2EssentiaStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Mod(modid = TAE2.MODID, version = Tags.VERSION, name = "Thaumium AE2", acceptedMinecraftVersions = "[1.7.10]")
public class TAE2 {

    public static final String MODID = "ThaumiumAE2";


    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "com.ThaumiumAE2.ThaumiumAE2.ClientProxy", serverSide = "com.ThaumiumAE2.ThaumiumAE2.CommonProxy")
    public static CommonProxy proxy;
    public static StorageChannel ESSENTIA_STORAGE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        EssentiaProvider.provide();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        ItemRegister.register(new ItemThaumiumCell(100, 200));
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
