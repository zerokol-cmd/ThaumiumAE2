package com.ThaumiumAE2.ThaumiumAE2.implementation;

import appeng.api.networking.*;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPostCacheConstruction;
import appeng.api.storage.ICellContainer;
import appeng.tile.storage.TileDrive;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.ThaumiumAE2.ThaumiumAE2.contents.terminals.GUIEssentiaTerminal;
import com.ThaumiumAE2.api.IEssentiaGridCache;

import java.util.ArrayList;
import java.util.Collection;

public class EssentiaGridCache implements IEssentiaGridCache {
    /**
     *
     */
    EssentiaNetwork network = new EssentiaNetwork();

    IGrid myGrid;


    public EssentiaGridCache(final IGrid g) {
        this.myGrid = g;
    }

    private void update() {
        network.updateCells();
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
        if (machine instanceof ICellContainer provider) {
            network.removeProvider(provider);
        }
    }

    /**
     * @param gridNode added to grid node
     * @param machine  to be added machine
     */
    //TODO:
    @Override
    public void addNode(IGridNode gridNode, IGridHost machine) {
        if (machine instanceof ICellContainer provider) {
            if (provider instanceof TileDrive drive) {
                drive.onReady();
            }
            network.addProvider(provider);
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
        if (network == null) {
            this.network = new EssentiaNetwork();
        }
        return network;
    }
}
