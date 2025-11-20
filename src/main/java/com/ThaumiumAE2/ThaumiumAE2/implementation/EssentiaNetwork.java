package com.ThaumiumAE2.ThaumiumAE2.implementation;

import appeng.api.config.Actionable;
import appeng.api.storage.ICellContainer;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.ThaumiumAE2.ThaumiumAE2.contents.essentiaCell.AspectCellInventory;
import com.ThaumiumAE2.api.IEssentiaNetwork;
import com.ThaumiumAE2.api.ITAE2EssentiaStack;
import thaumcraft.api.aspects.Aspect;

import java.util.*;

public class EssentiaNetwork implements IEssentiaNetwork {
    Collection<ICellContainer> cellProviders = new ArrayList<>();

    EssentiaNetwork() {
    }

    private AspectCellInventory findAvailableCell() {
        for (var provider : cellProviders) {
            var cells = provider.getCellArray(TAE2.ESSENTIA_STORAGE);

            for (var cell : cells) {
                if (cell instanceof AspectCellInventory essentiaCellInv) {
                    if (essentiaCellInv.getFreeTypes() > 0 && essentiaCellInv.getFreeBytes() > 0) {
                        return essentiaCellInv;
                    }
                }
            }
        }
        return null;
    }

    private AspectCellInventory getCellInventoryWithAspect(Aspect aspect) {
        for (var provider : cellProviders) {
            var cells = provider.getCellArray(TAE2.ESSENTIA_STORAGE);

            for (var cell : cells) {
                if (cell instanceof AspectCellInventory essentiaCellInv) {
                    if (essentiaCellInv.canAcceptAspect(aspect)) {
                        return essentiaCellInv;
                    }
                }
            }
        }

        return null; // nothing found
    }

    @Override
    public long extractEssentia(Aspect aspect, long amount, boolean simulate) {
        AspectCellInventory inv = getCellInventoryWithAspect(aspect);
        if (inv == null) return 0;
        //TODO: maybe, just maybe, we should return an amount of aspect transferred, not some random essentia stack??
        return inv.extractItems((ITAE2EssentiaStack) new EssentiaStack(aspect, amount), Actionable.MODULATE, null).getStackSize();
    }

    @Override
    public long injectEssentia(Aspect aspect, long amount, boolean simulate) {

        AspectCellInventory inv = getCellInventoryWithAspect(aspect);
        if (inv == null) {
            inv = findAvailableCell();
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
