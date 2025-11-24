package com.ThaumiumAE2.ThaumiumAE2.implementation.gui;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.layout.Grid;

import java.util.ArrayList;
import java.util.List;

public class EssentiaGridWidget extends ParentWidget<EssentiaGridWidget> implements Interactable {

    private EssentiaGridWidgetSyncHandler syncHandler;
    private Grid grid;
    private int initialSlots;

    public EssentiaGridWidget(int columns, int rows) {
        this.initialSlots = columns * columns;
        this.grid = new Grid();
//        this.grid.matrix()
        VerticalScrollData scrollData = new VerticalScrollData();
        scrollData.setCancelScrollEdge(true);
        grid.scrollable(scrollData);
        sizeRel(1);
//        this.grid.sizeRel(1);
//        this.grid.size(18 * columns, 18 * rows );//crutch
//        this.size(18 * columns, 18 * rows );//TODO: *8 is a crotch  crutch
        this.grid.sizeRel(0.8F, 0.45F);
        List<EssentiaSlot> rowSlots = new ArrayList<>();
        for (int i = 0; i < initialSlots; i++) {
            rowSlots.add(new EssentiaSlot());
        }
        this.grid.mapTo(columns, rowSlots);
        this.grid.pos(0,0);
//        this.grid.scrollable();
        this.grid.horizontalCenter();
//        sizeRel(1); this.grid.sizeRel(1);
//        this.grid.size(18 * columns, 18 * rows);
//        this.size(18 * columns, 18 * rows);


        // Add grid to this widget
        this.child(grid);
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
