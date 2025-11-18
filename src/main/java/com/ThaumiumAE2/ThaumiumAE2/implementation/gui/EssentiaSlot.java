package com.ThaumiumAE2.ThaumiumAE2.implementation.gui;

import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;

public class EssentiaSlot extends Widget<EssentiaSlot> implements Interactable{
    public static final int DEFAULT_SIZE = 18;
    EssentiaSlotSyncHandler syncHandler;
    public EssentiaSlot() {
        size(DEFAULT_SIZE);

        tooltip().setAutoUpdate(true);//.setHasTitleMargin(true);
        tooltipBuilder(this::addToolTip);
    }
    protected void addToolTip(RichTooltip tooltip) {
        tooltip.add("Essentia slot");
    }
    public EssentiaSlot syncHandler(EssentiaSlotSyncHandler syncHandler) {
        setSyncHandler(syncHandler);
        this.syncHandler = syncHandler;
        return this;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.draw(context);
        //custom draw
    }
}
