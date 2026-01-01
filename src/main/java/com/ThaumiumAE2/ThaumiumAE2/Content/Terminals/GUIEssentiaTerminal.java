package com.ThaumiumAE2.ThaumiumAE2.Content.Terminals;

import com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI.EssentiaGridWidget;
import com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI.EssentiaGridWidgetSyncHandler;
import com.ThaumiumAE2.ThaumiumAE2.Interfaces.IEssentiaNetwork;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.factory.inventory.ItemHandler;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidTank;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;

public class GUIEssentiaTerminal implements IGuiHolder<SidedPosGuiData> {
    @Nullable
    IEssentiaNetwork essentiaNetwork;

    GUIEssentiaTerminal() {
    }

    GUIEssentiaTerminal(IEssentiaNetwork essentiaNetwork) {
        this.essentiaNetwork = essentiaNetwork;
    }

    private static ButtonWidget<?> button(String text) {
        return new ButtonWidget<>()
            .size(16).margin(0, 1)
            .overlay();
    }

    @Override
    public ModularPanel buildUI(SidedPosGuiData posData, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("gui_essentia_terminal");

        var gridSync = new EssentiaGridWidgetSyncHandler(essentiaNetwork);
        syncManager.syncValue("essentia_grid", gridSync);

        if (essentiaNetwork != null) {
            Aspect.getCompoundAspects().forEach(aspect ->
                this.essentiaNetwork.injectEssentia(aspect, 1, false)
            );
        }

        panel.width(176);
        panel.height(200);
        panel.bindPlayerInventory();
        panel.child(
            new EssentiaGridWidget(syncManager, 9, 6)
                .syncHandler(gridSync)
                .horizontalCenter()
                .top(15)
        );
        panel.child(button("Dump all.").syncHandler(new InteractionSyncHandler().setOnMousePressed(data -> {
            if (!NetworkUtils.isClient()) {
                System.out.println("Dump all pressed.");
            }
        })).posRel(1, 0));

        return panel;
    }
}
