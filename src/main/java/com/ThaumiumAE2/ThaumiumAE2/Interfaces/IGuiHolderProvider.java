package com.ThaumiumAE2.ThaumiumAE2.Interfaces;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiData;

public interface IGuiHolderProvider<T extends GuiData> {
    public IGuiHolder<T> getGuiHolder();
}
