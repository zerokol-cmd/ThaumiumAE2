package com.ThaumiumAE2.ThaumiumAE2.implementation;

import appeng.api.config.Actionable;
import appeng.api.storage.ICellContainer;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.ThaumiumAE2.ThaumiumAE2.contents.essentiaCell.AspectCellInventory;
import com.ThaumiumAE2.api.IEssentiaNetwork;
import thaumcraft.api.aspects.Aspect;

import java.util.Collection;
import java.util.List;

public class EssentiaNetwork implements IEssentiaNetwork {
    Collection<ICellContainer> cellProviders;

    EssentiaNetwork() {
    }

    @Override
    public long extractEssentia(Aspect aspect, long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long injectEssentia(Aspect aspect, long amount, boolean simulate) {
        for (ICellContainer container : cellProviders) {
            var cells = container.getCellArray(TAE2.ESSENTIA_STORAGE);
            cells.iterator().forEachRemaining(inv -> {
                if (inv instanceof AspectCellInventory inv2) {
                    inv2.injectItems(new EssentiaStack(aspect, amount), Actionable.MODULATE, null);
                }
            });
        }
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


    public void update(Collection<ICellContainer> cellProviders) {
        this.cellProviders = cellProviders;
    }
}
