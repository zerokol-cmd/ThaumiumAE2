package com.ThaumiumAE2.ThaumiumAE2.contents.terminals;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import com.ThaumiumAE2.ThaumiumAE2.implementation.gui.EssentiaGridWidget;
import com.ThaumiumAE2.ThaumiumAE2.implementation.gui.EssentiaGridWidgetSyncHandler;
import com.ThaumiumAE2.api.IEssentiaNetwork;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;

public class GUIEssentiaTerminal implements IGuiHolder<SidedPosGuiData> {
    @Nullable
    IEssentiaNetwork essentiaNetwork;

    GUIEssentiaTerminal() {
    }

    GUIEssentiaTerminal(IEssentiaNetwork essentiaNetwork) {
        this.essentiaNetwork = essentiaNetwork;
    }


    @Override
    public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("tutorial_gui");

        var gridSync = new EssentiaGridWidgetSyncHandler(() ->
            essentiaNetwork.getStoredEssentia()
        );
        syncManager.syncValue("essentia_grid", gridSync);
        List<EssentiaStack> essentiaData = new ArrayList<>();
        if (essentiaNetwork != null) {
            Aspect.getCompoundAspects().forEach(aspect ->
                this.essentiaNetwork.injectEssentia(aspect, 1, false)
            );

            essentiaData.addAll(this.essentiaNetwork.getStoredEssentia());
        }

        panel.width(176);
        panel.height(200);

        panel.bindPlayerInventory();
        panel.child(
            new EssentiaGridWidget(syncManager,9, 6).syncHandler(gridSync).horizontalCenter().top(15)
        );

        return panel;
    }
}
