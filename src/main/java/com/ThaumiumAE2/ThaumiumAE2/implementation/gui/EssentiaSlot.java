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

    public EssentiaSlot() {
        size(DEFAULT_SIZE);
        tooltip().setAutoUpdate(true);//.setHasTitleMargin(true);
        tooltipBuilder(this::addToolTip);
    }

    protected void addToolTip(RichTooltip tooltip) {
        if (this.syncHandler.getValue() != null) {
            Aspect aspect = this.syncHandler.getValue().getAspect();
            tooltip.textColor(aspect.getColor());
            tooltip.add(aspect.getName());
        }

    }

    public EssentiaSlot syncHandler(EssentiaSlotSyncHandler syncHandler) {
        setSyncHandler(syncHandler);
        this.syncHandler = syncHandler;
        return this;
    }

    public EssentiaSlot value(EssentiaStack newValue) {
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
        UtilsFX.drawTag(1, 1, stack.getAspect(), stack.getStackSize(), 0, z,771,1.f,false);
    }
    private static void cleanupGLState() {
        // Reset matrix stacks
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glLoadIdentity();
//        GL11.glMatrixMode(GL11.GL_PROJECTION);
//        GL11.glLoadIdentity();
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//
//        // Reset color
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//        // Reset blending
//        GL11.glDisable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//
//        // Reset alpha test
//        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
//        GL11.glDisable(GL11.GL_ALPHA_TEST);
//
//        // Reset depth
//        GL11.glDisable(GL11.GL_DEPTH_TEST);
//        GL11.glDepthMask(true);
//        GL11.glDepthFunc(GL11.GL_LEQUAL);
//
//        // Reset lighting
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHT1);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
//
//        // Reset textures
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
//
//        // Reset fog
        GL11.glDisable(GL11.GL_FOG);
//
//        // Reset culling
        GL11.glDisable(GL11.GL_CULL_FACE);

        // Reset polygon offset
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(0.0F, 0.0F);

        // Reset scissor test
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

//         Reset line width and point size
//        GL11.glLineWidth(1.0F);
//        GL11.glPointSize(1.0F);
    }
    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        if (this.syncHandler.getValue() == null) return;
        cleanupGLState();

        try {
            EssentiaStack stack = this.syncHandler.getValue();

            if (stack != null && stack.getAspect() != null) {

//                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                // Enable proper blending
//                GlStateManager.enableBlend();
//                GlStateManager.tryBlendFuncSeparate(
//                    GL11.GL_SRC_ALPHA,
//                    GL11.GL_ONE_MINUS_SRC_ALPHA,
//                    GL11.GL_ONE,
//                    GL11.GL_ZERO
//                );
                GlStateManager.enableTexture2D();

                drawAspect(stack, context.getCurrentDrawingZ());
            }

            // Draw slot background/border if needed
            // GuiDraw.drawBorder(...);

        } finally {
            // Always restore GL state, even if exception occurs
//            GL11.glPopMatrix();
//            GL11.glPopAttrib();
//
//            // Explicitly reset commonly problematic states
//            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//            GlStateManager.disableBlend();
//            GlStateManager.enableLighting();
        }

    }

}
