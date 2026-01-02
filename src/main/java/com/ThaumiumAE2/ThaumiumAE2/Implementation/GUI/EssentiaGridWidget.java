package com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI;

import com.ThaumiumAE2.ThaumiumAE2.Implementation.EssentiaStack;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.ColorType;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EssentiaGridWidget extends ParentWidget<EssentiaGridWidget> implements Interactable {
    private EssentiaGridWidgetSyncHandler syncHandler;
    private Grid grid;
    private final int initialSlots;

    public EssentiaGridWidget(PanelSyncManager syncManager, int columns, int rows) {
        this.initialSlots = columns * rows;
        this.grid = new Grid();

        CustomVerticalScrollData scrollData = new CustomVerticalScrollData(false, 9);
        scrollData.setCancelScrollEdge(true);
        scrollData.setScrollSize(1);
        scrollData.texture(new UITexture(new ResourceLocation("thaumiumae2", "textures/gui/scroll.png"), 0, 0, 1, 1, null, true));

        grid.scrollable(scrollData);

        heightRel(0.5f);
        this.grid.width(columns * 18 + 10);

        this.grid.heightRel(1.F);
        List<EssentiaSlot> rowSlots = new ArrayList<>();
        for (int i = 0; i < initialSlots; i++) {
            rowSlots.add(new EssentiaGridSlot(this, i));
            syncManager.syncValue("essentia_grid_widget_slot", rowSlots.get(i).syncHandler);
        }
        this.grid.mapTo(columns, rowSlots);
        this.grid.pos(0, 0);
        this.grid.horizontalCenter();

        this.child(grid);
    }

    /**
     * @return
     */
    @Override
    public @NotNull SyncHandler getSyncHandler() {
        return this.syncHandler;
    }

    /**
     * Attach sync handler and set up update callback
     */

    public EssentiaGridWidget syncHandler(EssentiaGridWidgetSyncHandler handler) {
        this.syncHandler = handler;
        handler.setUpdateCallback(this::updateGrid);
        updateGrid(handler.getValue());
        return this;
    }

    /**
     * Update grid with new data
     * DO NOT resize during updates - all slots must exist at construction time
     */
    private void updateGrid(List<EssentiaStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            invalidateGrid();
            return;
        }

        // Clear all slots first
        invalidateGrid();

        // Update slots with new data
        int slotIndex = 0;
        for (var widget : grid.getChildren()) {
            if (!(widget instanceof EssentiaSlot slot)) {
                continue;
            }

            if (slotIndex < stacks.size()) {
                EssentiaStack stack = stacks.get(slotIndex);
                if (stack != null) {
                    slot.value(stack);
                }
            }

            slotIndex++;

            // Don't update more slots than we have
            if (slotIndex >= initialSlots) {
                break;
            }
        }
    }

    /**
     * Clear all slots
     */
    private void invalidateGrid() {
        grid.getChildren().forEach(widget -> {
            if (widget instanceof EssentiaSlot slot) {
                slot.makeEmpty();
            }
        });
    }

    public EssentiaSlot getSlotById(int id) {
        return (EssentiaSlot) grid.getChildren().get(id);
    }

    /**
     * Manual update (for server-side use)
     */
    public EssentiaGridWidget updateView(List<EssentiaStack> newValue) {
        if (syncHandler != null) {
            syncHandler.setValue(newValue, true, true);
        }
        return this;
    }
}
