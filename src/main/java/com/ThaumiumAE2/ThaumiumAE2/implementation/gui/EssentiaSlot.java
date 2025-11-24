package com.ThaumiumAE2.ThaumiumAE2.implementation.gui;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.GlStateManager;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

import java.util.function.Consumer;
import java.util.function.Supplier;

//todo:
//
public class EssentiaSlot extends Widget<EssentiaSlot> implements Interactable {
    public static final int DEFAULT_SIZE = 18;
    EssentiaSlotSyncHandler syncHandler = new EssentiaSlotSyncHandler();
    Supplier<EssentiaStack> essentiaSupplier;
    boolean isEmpty = false;

    public EssentiaSlot() {
        this.isEmpty = true;
        size(DEFAULT_SIZE);
        tooltip().setAutoUpdate(true);//.setHasTitleMargin(true);
        tooltipBuilder(this::addToolTip);
    }

    protected void addToolTip(RichTooltip tooltip) {
        if (this.syncHandler.getValue() != null && !isEmpty) {
            Aspect aspect = this.syncHandler.getValue().getAspect();
            tooltip.textColor(aspect.getColor());
            tooltip.add(aspect.getName());
        }

    }

    public EssentiaSlot syncHandler(EssentiaSlotSyncHandler syncHandler) {

        this.isEmpty = true;
        setSyncHandler(syncHandler);
        this.syncHandler = syncHandler;
        return this;
    }

    public EssentiaSlot makeEmpty() {
        isEmpty = true;
        return this;
    }
    public EssentiaSlot value(EssentiaStack newValue) {
        isEmpty = false;
        this.syncHandler.setValue(newValue);
        syncHandler.notifyUpdate();
        return this;
    }

    @Override
    public boolean isValidSyncHandler(SyncHandler syncHandler) {
        this.syncHandler = castIfTypeElseNull(syncHandler, EssentiaSlotSyncHandler.class);
        return this.syncHandler != null;
    }

    private void drawAspect(EssentiaStack stack, int z) {
        if (stack == null ) return;
        UtilsFX.drawTag(1, 1, stack.getAspect(), stack.getStackSize(), 0, z, 771, 1.f, false);
    }


    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        if (this.syncHandler.getValue() == null){
            isEmpty = true;
        }

        EssentiaStack stack = this.syncHandler.getValue();

        if (stack != null && stack.getAspect() != null) {

            GlStateManager.enableTexture2D();

            drawAspect(stack, context.getCurrentDrawingZ());
        }


    }

}
