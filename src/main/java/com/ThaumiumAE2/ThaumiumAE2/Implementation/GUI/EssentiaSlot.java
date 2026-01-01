package com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI;

import com.ThaumiumAE2.ThaumiumAE2.Implementation.EssentiaStack;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.GlStateManager;
import com.cleanroommc.modularui.utils.MouseData;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.Widget;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

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
        if (stack == null) return;
        UtilsFX.drawTag(1, 1, stack.getAspect(), stack.getStackSize(), 0, z, 771, 1.f, false);
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        if (!this.syncHandler.canFill() && !this.syncHandler.canDrain()) {
            return Result.ACCEPT;
        }
        ItemStack cursorStack = Platform.getClientPlayer().inventory.getItemStack();
        if (cursorStack != null) {
            MouseData mouseData = MouseData.create(mouseButton);
            if(this.syncHandler.isValid()) {
                this.syncHandler.syncToServer(EssentiaSlotSyncHandler.SYNC_CLICK, mouseData::writeToPacket);
            }else{
                return Result.ACCEPT;
            }
        }
        return Result.SUCCESS;
    }

    private void drawEmptySlot() {
        Tessellator tessellator = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(0.25f, 0.25f, 0.25f, 0.8f);

        int x = 0;
        int y = 0;
        int width = 16;
        int height = 16;
        int borderWidth = 1;

        tessellator.startDrawingQuads();

        tessellator.addVertex(x, y + borderWidth, 0);
        tessellator.addVertex(x + width, y + borderWidth, 0);
        tessellator.addVertex(x + width, y, 0);
        tessellator.addVertex(x, y, 0);

        tessellator.addVertex(x, y + height, 0);
        tessellator.addVertex(x + width, y + height, 0);
        tessellator.addVertex(x + width, y + height - borderWidth, 0);
        tessellator.addVertex(x, y + height - borderWidth, 0);

        tessellator.addVertex(x, y + height, 0);
        tessellator.addVertex(x + borderWidth, y + height, 0);
        tessellator.addVertex(x + borderWidth, y, 0);
        tessellator.addVertex(x, y, 0);

        tessellator.addVertex(x + width - borderWidth, y + height, 0);
        tessellator.addVertex(x + width, y + height, 0);
        tessellator.addVertex(x + width, y, 0);
        tessellator.addVertex(x + width - borderWidth, y, 0);

        tessellator.draw();

        // Optional: Draw darker background inside the slot
        GL11.glColor4f(0.15f, 0.15f, 0.15f, 0.5f);

        tessellator.startDrawingQuads();
        tessellator.addVertex(x + borderWidth, y + height - borderWidth, 0);
        tessellator.addVertex(x + width - borderWidth, y + height - borderWidth, 0);
        tessellator.addVertex(x + width - borderWidth, y + borderWidth, 0);
        tessellator.addVertex(x + borderWidth, y + borderWidth, 0);
        tessellator.draw();

        // Re-enable textures and reset color
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        if (this.syncHandler.getValue() == null) {
            isEmpty = true;
        }
        EssentiaStack stack = this.syncHandler.getValue();

        if (stack != null && stack.getAspect() != null && !isEmpty) {

//            drawEmptySlot();
            GlStateManager.enableTexture2D();
            drawAspect(stack, context.getCurrentDrawingZ());
        }
    }

    @Override
    public void drawOverlay(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawOverlay(context, widgetTheme);
    }

}
