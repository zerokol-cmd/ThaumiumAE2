package com.ThaumiumAE2.ThaumiumAE2.contents.terminals;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaNetwork;
import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GUIEssentiaTerminal implements IGuiHolder<SidedPosGuiData> {
    @Nullable
    IEssentiaNetwork essentiaNetwork;
    EssentiaSlotSyncHandler syncHandler;

    GUIEssentiaTerminal() {
    }

    GUIEssentiaTerminal(IEssentiaNetwork essentiaNetwork) {
        this.essentiaNetwork = essentiaNetwork;
        this.essentiaNetwork.injectEssentia(Aspect.AIR, 10, false);
    }


    @Override
    public ModularPanel buildUI(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("tutorial_gui");

        syncManager.registerSlotGroup("tutorial_fluid", 1);
//        syncHandler.setValue(new EssentiaStack(Aspect.AIR, 10));

        panel.bindPlayerInventory();  // Adds player item inventory/hotbar
        panel.child(
            SlotGroupWidget.builder()
                .matrix("FE")  // Defines 2 rows x 6 cols layout
                .key('E', index -> {  // 'F' = fluid slot; index auto-assigned 0-11 row-major
                    return new EssentiaSlot()
                        .syncHandler(new EssentiaSlotSyncHandler(
                            null, () -> {
                            if (essentiaNetwork == null) return null;
                            if (!essentiaNetwork.getStoredEssentia().isEmpty()) return null;

                            return this.essentiaNetwork.getStoredEssentia().get(0);
                        }
                        ));
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
