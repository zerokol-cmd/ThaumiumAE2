package com.ThaumiumAE2.ThaumiumAE2.Registers;

import com.ThaumiumAE2.ThaumiumAE2.contents.Terminals.ItemEssentiaTerminal;
import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.aspects.Aspect;

public class ItemRegister {

    public static void register() {
        GameRegistry.registerItem(new com.ThaumiumAE2.ThaumiumAE2.contents.EssentiaCell.ItemThaumiumCell(100, 200), com.ThaumiumAE2.ThaumiumAE2.contents.EssentiaCell.ItemThaumiumCell.ID);
        GameRegistry.registerItem(new com.ThaumiumAE2.ThaumiumAE2.contents.Terminals.ItemEssentiaTerminal(), com.ThaumiumAE2.ThaumiumAE2.contents.Terminals.ItemEssentiaTerminal.ID);
    }
}
