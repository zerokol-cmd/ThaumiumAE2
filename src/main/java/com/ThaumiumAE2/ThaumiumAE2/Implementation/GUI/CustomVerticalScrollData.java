package com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.widget.scroll.ScrollArea;
import com.cleanroommc.modularui.widget.scroll.ScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;

public class CustomVerticalScrollData extends VerticalScrollData {

    @Override
    public void drawScrollbar(ScrollArea area, ModularGuiContext context, WidgetTheme widgetTheme, IDrawable texture) {
        System.out.println("рисуем сколл бар");
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
