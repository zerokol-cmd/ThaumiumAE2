package com.ThaumiumAE2.ThaumiumAE2.implementation;

import appeng.api.networking.*;
import appeng.api.storage.ICellContainer;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.ThaumiumAE2.api.IEssentiaGridCache;

import java.util.ArrayList;
import java.util.Collection;

public class EssentiaGridCache implements IEssentiaGridCache {
    /**
     *
     */
    EssentiaNetwork network = new EssentiaNetwork();

    IGrid myGrid;
    Collection<ICellContainer> cellProviders = new ArrayList<>();

    public EssentiaGridCache(final IGrid g) {
        this.myGrid = g;
    }
    private void update(){
        network.update(cellProviders);
    }
    @Override
    public void onUpdateTick() {
        update();
    }

    /**
     * @param gridNode removed from that grid
     * @param machine  to be removed machine
     */
    @Override
    public void removeNode(IGridNode gridNode, IGridHost machine) {
        if(cellProviders == null) return;
        if (machine instanceof ICellContainer provider) {
            cellProviders.remove(provider);
            update();
        }
    }

    /**
     * @param gridNode added to grid node
     * @param machine  to be added machine
     */
    @Override
    public void addNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof ICellContainer provider) {
            if(!provider.getCellArray(TAE2.ESSENTIA_STORAGE).isEmpty()) {
                cellProviders.add(provider);
                update();
            }
        }
    }

    /**
     * @param destinationStorage storage which receives half of old grid
     */
    @Override
    public void onSplit(IGridStorage destinationStorage) {

    }

    /**
     * @param sourceStorage old storage
     */
    @Override
    public void onJoin(IGridStorage sourceStorage) {

    }

    /**
     * @param destinationStorage storage
     */
    @Override
    public void populateGridStorage(IGridStorage destinationStorage) {

    }

    @Override
    public EssentiaNetwork getEssentiaNetwork() {
        return network;
    }
}
