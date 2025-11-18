package com.ThaumiumAE2.ThaumiumAE2.implementation;

import com.ThaumiumAE2.api.IEssentiaNetwork;
import thaumcraft.api.aspects.Aspect;

import java.util.List;

public class EssentiaNetwork implements IEssentiaNetwork {
    @Override
    public long extractEssentia(Aspect aspect, long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long injectEssentia(Aspect aspect, long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long getEssentiaAmount(Aspect aspect) {
        return 0;
    }

    @Override
    public List<EssentiaStack> getStoredEssentia() {
        return null;
    }
}
