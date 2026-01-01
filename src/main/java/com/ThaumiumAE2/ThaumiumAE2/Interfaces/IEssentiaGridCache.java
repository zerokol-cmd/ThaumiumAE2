package com.ThaumiumAE2.ThaumiumAE2.Interfaces;

import appeng.api.networking.IGridCache;
import com.ThaumiumAE2.ThaumiumAE2.Implementation.EssentiaNetwork;

public interface IEssentiaGridCache extends IGridCache {

    EssentiaNetwork getEssentiaNetwork();
}
