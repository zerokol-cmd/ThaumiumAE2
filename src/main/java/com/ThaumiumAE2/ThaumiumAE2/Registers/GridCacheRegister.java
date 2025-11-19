package com.ThaumiumAE2.ThaumiumAE2.Registers;

import appeng.api.AEApi;
import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaGridCache;
import com.ThaumiumAE2.api.IEssentiaGridCache;

public class GridCacheRegister {
    public static void register(){
        AEApi.instance().registries().gridCache().registerGridCache(IEssentiaGridCache.class, EssentiaGridCache.class);
    }
}
