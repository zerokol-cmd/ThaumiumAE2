package com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI;

import com.cleanroommc.modularui.api.GuiAxis;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.widget.scroll.ScrollArea;
import com.cleanroommc.modularui.widget.scroll.ScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;

public class CustomVerticalScrollData extends VerticalScrollData {

    public CustomVerticalScrollData(boolean leftAlignment, int thickness) {
        super(leftAlignment, thickness);
        this.setScrollSpeed(1);
    }

    @Override
    public int getScrollBarLength(ScrollArea area) {
        return 46;
    }

    @Override
    public boolean onMouseClicked(ScrollArea area, int mainAxisPos, int crossAxisPos, int button) {
        int areaCrossSize = area.getSize(this.getAxis().getOther());
        boolean isClickInBar = isOnAxisStart()
            ? crossAxisPos <= getThickness()
            : crossAxisPos >= areaCrossSize - getThickness();

        if (!isClickInBar) {
            return false;
        }

        boolean isOtherActive = isOtherScrollBarActive(area, true);
        int fullVisibleSize = getFullVisibleSize(area, isOtherActive);
        int thumbSize = getScrollBarLength(area);

        ScrollData data2 = getOtherScrollData(area);
        int trackStartOffset = 0;
        if (data2 != null && isOtherActive && data2.isOnAxisStart()) {
            trackStartOffset = data2.getThickness();
        }

        int localClickPos = mainAxisPos - trackStartOffset;
        int currentThumbPos = getScrollBarStart(area, thumbSize, fullVisibleSize);

        boolean clickInsideThumb = localClickPos >= currentThumbPos && localClickPos <= currentThumbPos + thumbSize;

        this.dragging = true;

        if (clickInsideThumb) {
            this.clickOffset = localClickPos - currentThumbPos;
        } else {
            this.clickOffset = thumbSize / 2;
            int newThumbVisualPos = localClickPos - this.clickOffset;
            int trackLength = fullVisibleSize - thumbSize;
            int scrollableHeight = getScrollSize() - fullVisibleSize;

            if (trackLength > 0) {
                int newScroll = (int) ((long) newThumbVisualPos * scrollableHeight / trackLength);
                scrollTo(area, newScroll);
            }
        }

        return true;
    }

    @Override
    public void drawScrollbar(ScrollArea area, ModularGuiContext context, WidgetTheme widgetTheme, IDrawable texture) {
        boolean isOtherActive = isOtherScrollBarActive(area, true);
        int l = this.getScrollBarLength(area);
        int x = isOnAxisStart() ? 0 : area.w() - getThickness();
        int y = 0;
        int w = getThickness();
        int h = area.height;

        GuiDraw.drawRect(x, y, w, h, area.getScrollBarBackgroundColor());
        y = getScrollBarStart(area, l, isOtherActive);
        ScrollData data2 = getOtherScrollData(area);
        if (data2 != null && isOtherActive && data2.isOnAxisStart()) {
            y += data2.getThickness();
        }
        h = l;
        drawScrollBar(context, x, y, w, h, widgetTheme, texture);
    }
}
