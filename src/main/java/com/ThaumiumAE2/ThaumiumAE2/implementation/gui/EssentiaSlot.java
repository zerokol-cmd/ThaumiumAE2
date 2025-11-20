package com.ThaumiumAE2.ThaumiumAE2.implementation.gui;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;

import java.util.function.Consumer;
import java.util.function.Supplier;

//todo:
//
public class EssentiaSlot extends Widget<EssentiaSlot> implements Interactable {
    public static final int DEFAULT_SIZE = 18;
    EssentiaSlotSyncHandler syncHandler;
    Supplier<EssentiaStack> essentiaSupplier;
    public EssentiaSlot() {
        size(DEFAULT_SIZE);
        tooltip().setAutoUpdate(true);//.setHasTitleMargin(true);
        tooltipBuilder(this::addToolTip);
    }

    protected void addToolTip(RichTooltip tooltip) {
        tooltip.add("Essentia slot");
        if (this.syncHandler.getValue() != null) {
            tooltip.add(this.syncHandler.getValue().getAspect().getName());
        }

    }

    public EssentiaSlot syncHandler(EssentiaSlotSyncHandler syncHandler) {
        setSyncHandler(syncHandler);
        this.syncHandler = syncHandler;
        return this;
    }

    public EssentiaSlot value(EssentiaStack newValue){
        this.syncHandler.setValue(newValue);
        return this;
    }

    @Override
    public boolean isValidSyncHandler(SyncHandler syncHandler) {
        this.syncHandler = castIfTypeElseNull(syncHandler, EssentiaSlotSyncHandler.class);
        return this.syncHandler != null;
    }


    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
//        super.draw(context);

    }

}
