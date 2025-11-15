package com.ThaumiumAE2.ThaumiumAE2;

import appeng.api.AEApi;
import com.ThaumiumAE2.ThaumiumAE2.Items.ItemThaumiumCell;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        TAE2.LOG.info(Config.greeting);
        TAE2.LOG.info("I am MyMod at version " + Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {
//        AEApi.instance().registries().cell().addCellHandler(new ItemThaumiumCell());
    }

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}
}
