package com.ThaumiumAE2.ThaumiumAE2.Proxies;

import com.ThaumiumAE2.ThaumiumAE2.Implementation.TextureManager;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(new TextureManager.TextureEventHandler());
    }
}
