package com.ThaumiumAE2.ThaumiumAE2.Registers;

import com.ThaumiumAE2.ThaumiumAE2.common.Gui.CablePartGuiFactory;
import com.cleanroommc.modularui.factory.GuiManager;

public class GUIFactoriesRegister {
    static public void register(){
        GuiManager.registerFactory(CablePartGuiFactory.INSTANCE);
    }
}
