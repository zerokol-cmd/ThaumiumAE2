package com.ThaumiumAE2.ThaumiumAE2;

import com.ThaumiumAE2.ThaumiumAE2.Items.ItemThaumiumCell;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ItemRegister {
    static void register(Item item){
        GameRegistry.registerItem(item,ItemThaumiumCell.ID);
    }
}
