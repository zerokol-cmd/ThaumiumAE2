package com.ThaumiumAE2.ThaumiumAE2.contents.terminals;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import com.ThaumiumAE2.ThaumiumAE2.implementation.gui.EssentiaGridWidget;
import com.ThaumiumAE2.ThaumiumAE2.implementation.gui.EssentiaGridWidgetSyncHandler;
import com.ThaumiumAE2.ThaumiumAE2.implementation.gui.EssentiaSlotSyncHandler;
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
    private EssentiaStack EMPTY = null;
    @Nullable
    IEssentiaNetwork essentiaNetwork;
    EssentiaSlotSyncHandler syncHandler;

    GUIEssentiaTerminal() {
    }

    GUIEssentiaTerminal(IEssentiaNetwork essentiaNetwork) {
        this.essentiaNetwork = essentiaNetwork;
    }

    static final int MAX_SLOTS = 81;

    @Override
    public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("tutorial_gui");

        // Step 1: Get essentia data on SERVER
        List<EssentiaStack> essentiaData = new ArrayList<>();
        if (essentiaNetwork != null) {
//            this.essentiaNetwork.injectEssentia(Aspect.AIR, 0, false);
//            this.essentiaNetwork.injectEssentia(Aspect.EARTH, 0, false);
//            this.essentiaNetwork.injectEssentia(Aspect.ORDER, 0, false);
//            this.essentiaNetwork.injectEssentia(Aspect.ENTROPY, 0, false);
//            this.essentiaNetwork.injectEssentia(Aspect.FIRE, 0, false);
//            for (int x = 0; x < 100; x++)
//                this.essentiaNetwork.injectEssentia(Aspect.WATER, 0, false);
            Aspect.getCompoundAspects().forEach(aspect ->
                this.essentiaNetwork.injectEssentia(aspect, 1, false)
            );

//            this.essentiaNetwork.injectEssentia(Aspect.VOID, 0, false);

            essentiaData.addAll(this.essentiaNetwork.getStoredEssentia());
        }
        panel.width(176);
        panel.heightRel(0.4f);

        var gridSync = new EssentiaGridWidgetSyncHandler(() ->
            essentiaNetwork.getStoredEssentia()
        );
        syncManager.syncValue("essentia_grid", gridSync);
        panel.bindPlayerInventory();
        panel.child(
            new EssentiaGridWidget(7, 3).syncHandler(gridSync).horizontalCenter().top(15)
        );
//        TAE2.LOG.info("Building the ui finished.");
//        for (int i = 0; i <10; i++) {
//            panel.child(
//                new EssentiaSlot().syncHandler(
//                    new EssentiaSlotSyncHandler(null,() ->
//                        new EssentiaStack(Aspect.ENTROPY, 0)
//                     )
//                ).pos(i*18,i*18)
//            );
//        }

        return panel;
    }
}
