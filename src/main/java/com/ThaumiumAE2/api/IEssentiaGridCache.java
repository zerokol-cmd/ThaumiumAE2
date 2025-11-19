package com.ThaumiumAE2.api;

import appeng.api.networking.IGridCache;
import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaNetwork;

public interface IEssentiaGridCache extends IGridCache {

    EssentiaNetwork getEssentiaNetwork();
}
