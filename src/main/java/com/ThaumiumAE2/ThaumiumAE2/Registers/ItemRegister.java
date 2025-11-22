package com.ThaumiumAE2.ThaumiumAE2.Registers;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemRegister {

    public static void register() {
        GameRegistry.registerItem(new com.ThaumiumAE2.ThaumiumAE2.contents.essentiaCell.ItemThaumiumCell(1024, 6), com.ThaumiumAE2.ThaumiumAE2.contents.essentiaCell.ItemThaumiumCell.ID);
        GameRegistry.registerItem(new com.ThaumiumAE2.ThaumiumAE2.contents.terminals.ItemEssentiaTerminal(), com.ThaumiumAE2.ThaumiumAE2.contents.terminals.ItemEssentiaTerminal.ID);
    }
}
