package com.ThaumiumAE2.ThaumiumAE2.Registers;

import com.ThaumiumAE2.ThaumiumAE2.implementation.gui.CablePartGuiFactory;
import com.cleanroommc.modularui.factory.GuiManager;

public class GUIFactoriesRegister {
    static public void register(){
        GuiManager.registerFactory(CablePartGuiFactory.INSTANCE);
    }
}
