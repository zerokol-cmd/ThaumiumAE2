package com.ThaumiumAE2.ThaumiumAE2.Registers;

import com.ThaumiumAE2.ThaumiumAE2.Items.Cells.EssentiaStack;
import net.minecraft.item.Item;

import com.ThaumiumAE2.ThaumiumAE2.Items.Cells.ItemThaumiumCell;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.aspects.Aspect;

public class ItemRegister {

    public static void register() {
        GameRegistry.registerItem(new ItemThaumiumCell(100, 200), ItemThaumiumCell.ID);

        GameRegistry.registerItem(new EssentiaStack(Aspect.EARTH,10), "sample_aspect");
    }
}
