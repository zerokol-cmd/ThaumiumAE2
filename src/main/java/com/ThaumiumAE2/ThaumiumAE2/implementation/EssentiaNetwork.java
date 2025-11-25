package com.ThaumiumAE2.ThaumiumAE2.implementation;

import appeng.api.storage.ICellContainer;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.ThaumiumAE2.ThaumiumAE2.contents.essentiaCell.AspectCellInventory;
import com.ThaumiumAE2.api.IAspectInventory;
import com.ThaumiumAE2.api.IEssentiaNetwork;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.*;
import java.util.stream.Collectors;

public class EssentiaNetwork implements IEssentiaNetwork {
    Collection<ICellContainer> cellContainers = new ArrayList<>();
    Collection<AspectCellInventory> essentiaCellInventories = new ArrayList<AspectCellInventory>();

    EssentiaNetwork() {
    }

    public void updateCells() {
        cellContainers.forEach(container -> {
            var cells = container.getCellArray(TAE2.ESSENTIA_STORAGE);
            essentiaCellInventories.clear();
            for (var cell : cells) {
                if (cell instanceof AspectCellInventory essentiaCellInv) {
                    essentiaCellInventories.add(essentiaCellInv);
                }
            }
        });
    }


    private AspectCellInventory findAvailableCell() {
        for (var provider : cellContainers) {
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
        for (var provider : cellContainers) {
            var cells = provider.getCellArray(TAE2.ESSENTIA_STORAGE);

            for (var cell : cells) {
                if (cell instanceof IAspectInventory essentiaCellInv) {
                    if (essentiaCellInv.canAccept(aspect)) {
                        return (AspectCellInventory) essentiaCellInv;
                    }
                }
            }
        }

        return null; // nothing found
    }

    @Override
    public long extractEssentia(Aspect aspect, long amount, boolean simulate) {

        // //TODO: use an interface. becauase we could have more than one type of essentia cell
        // AspectCellInventory inv = getCellInventoryWithAspect(aspect);
        // if (inv == null) return 0;

        // return inv.extractItems((ITAE2EssentiaStack) new EssentiaStack(aspect, amount), Actionable.MODULATE, null).getStackSize();

        long essentiaRemaining = amount;

        for (var provider : cellContainers) {
            var cells = provider.getCellArray(TAE2.ESSENTIA_STORAGE);

            for (var cell : cells) {
                if (cell instanceof IAspectInventory essentiaCellInv) {
                    long extracted = essentiaCellInv.extractEssentia(aspect, essentiaRemaining, simulate, null);
                    essentiaRemaining -= extracted;
                    if (essentiaRemaining <= 0) {
                        return amount - essentiaRemaining;
                    }
                }
            }
        }

        return amount - essentiaRemaining;
    }

    @Override
    public long injectEssentia(Aspect aspect, long amount, boolean simulate) {
        long essentiaRemaining = amount;

        for (var provider : cellContainers) {
            var cells = provider.getCellArray(TAE2.ESSENTIA_STORAGE);

            for (var cell : cells) {
                if (cell instanceof IAspectInventory essentiaCellInv) {
                    long injected = essentiaCellInv.injectEssentia(aspect, essentiaRemaining, simulate, null);
                    essentiaRemaining -= injected;
                    if (essentiaRemaining <= 0) {
                        return essentiaRemaining;//ахуенный брейк вынеси в отдельную функцию чтобы такого не было и/или просто ретёрн
                    }
                }
            }
        }

        return essentiaRemaining;
    }

    @Override
    public long getEssentiaAmount(Aspect aspect) {
        long essentiaTotal = 0;

        for (var provider : cellContainers) {
            var cells = provider.getCellArray(TAE2.ESSENTIA_STORAGE);
            for (var cell : cells) {
                if (cell instanceof IAspectInventory essentiaCellInv) {
                    essentiaTotal += essentiaCellInv.getEssentiaAmount(aspect);
                }
            }
        }

        return essentiaTotal;
    }

    private List<EssentiaStack> aspectListToEssentiaStackList(AspectList list) {
        List<EssentiaStack> result = new ArrayList<>();
        list.aspects.forEach((aspect, count) -> {
            result.add(new EssentiaStack(aspect, count));
        });
        return result;
    }

    @Override
    public List<EssentiaStack> getStoredEssentia() {
        Map<Aspect, Long> aspectTotals = new HashMap<>();

        for (var provider : cellContainers) {
            var cells = provider.getCellArray(TAE2.ESSENTIA_STORAGE);
            for (var cell : cells) {
                if (cell instanceof IAspectInventory inv) {
                    List<EssentiaStack> storedEssentia = aspectListToEssentiaStackList(inv.getStoredEssentia());
                    for (EssentiaStack stack : storedEssentia) {
                        aspectTotals.merge(stack.getAspect(), stack.getStackSize(), Long::sum);
                    }
                }
            }
        }

        return aspectTotals.entrySet().stream()
            .map(entry -> new EssentiaStack(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public void addProvider(ICellContainer provider) {
        this.cellContainers.add(provider);
    }

    public void removeProvider(ICellContainer provider) {
        this.cellContainers.remove(provider);
    }
}
