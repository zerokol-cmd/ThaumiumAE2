package com.ThaumiumAE2.ThaumiumAE2.contents.terminals;

import com.ThaumiumAE2.ThaumiumAE2.implementation.gui.EssentiaSlot;
import com.ThaumiumAE2.ThaumiumAE2.implementation.gui.EssentiaSlotSyncHandler;
import com.ThaumiumAE2.api.IEssentiaNetwork;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.FluidTankHandler;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GUIEssentiaTerminal implements IGuiHolder<SidedPosGuiData> {
    @Nullable
    IEssentiaNetwork essentiaNetwork;

    GUIEssentiaTerminal(IEssentiaNetwork essentiaNetwork) {
        this.essentiaNetwork = essentiaNetwork;
    }


    @Override
    public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("tutorial_gui");

        syncManager.registerSlotGroup("tutorial_fluid", 1);

        panel.bindPlayerInventory();  // Adds player item inventory/hotbar

        // Build the 6x2 fluid grid via SlotGroupWidget.matrix
        panel.child(
            SlotGroupWidget.builder()
                .matrix("FFFFFF", "FFFFFF")  // Defines 2 rows x 6 cols layout
                .key('F', index -> {  // 'F' = fluid slot; index auto-assigned 0-11 row-major
                    return new EssentiaSlot()
                        .syncHandler(new EssentiaSlotSyncHandler(essentiaNetwork));
                })
                .build()
        );

        // Add sort button next to the grid (e.g., at x=7 + 6*18 + 2 = 117, y=18)
        panel.child(
            new ButtonWidget().syncHandler(
                new InteractionSyncHandler().setOnMousePressed(a -> {
//                    sortTanks();
                })
            )
        );
        return panel;
    }
}
