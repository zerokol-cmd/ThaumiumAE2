package com.ThaumiumAE2.ThaumiumAE2.Registers;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemRegister {

    public static void register() {
        GameRegistry.registerItem(new com.ThaumiumAE2.ThaumiumAE2.Content.EssentiaCell.ItemThaumiumCell(1024, 69), com.ThaumiumAE2.ThaumiumAE2.Content.EssentiaCell.ItemThaumiumCell.ID);
        GameRegistry.registerItem(new com.ThaumiumAE2.ThaumiumAE2.Content.Terminals.ItemEssentiaTerminal(), com.ThaumiumAE2.ThaumiumAE2.Content.Terminals.ItemEssentiaTerminal.ID);
    }
}
