package com.ThaumiumAE2.ThaumiumAE2.contents.Terminals;


import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// Implement IPartItem to link this Item to your IPart class
public class ItemEssentiaTerminal extends Item implements IPartItem {
    final static public String ID = "essentia_terminal";

    public ItemEssentiaTerminal() {
        this.setUnlocalizedName(ID);
        this.setTextureName(TAE2.MODID+":essentia_terminal"); // The texture for the item in inventory
    }

    @Override
    public IPart createPartFromItemStack(ItemStack stack) {
        return new PartEssentiaTerminal(stack);
    }
}
