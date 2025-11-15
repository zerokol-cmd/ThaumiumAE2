package com.ThaumiumAE2.ThaumiumAE2.Items;

import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.AEApi;
import appeng.api.storage.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemThaumiumCell extends Item implements ICellHandler {
    public ItemThaumiumCell() {
        AEApi.instance().registries().cell().addCellHandler(this);

        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public boolean isCell(ItemStack itemStack) {
        return itemStack.getItem() == this;
    }

    @Override
    public IMEInventoryHandler getCellInventory(ItemStack is, ISaveProvider host, StorageChannel channel) {
//        if ((channel != StorageChannel.FLUIDS) || !(essentiaCell.getItem() instanceof ItemThaumiumCell)) {
//            return null;
//        }

        return new AspectCellInventory();
    }

    @Override
    public IIcon getTopTexture_Light() {
        return null;
    }

    @Override
    public IIcon getTopTexture_Medium() {
        return null;
    }

    @Override
    public IIcon getTopTexture_Dark() {
        return null;
    }

    @Override
    public void openChestGui(EntityPlayer player, IChestOrDrive chest, ICellHandler cellHandler, IMEInventoryHandler inv, ItemStack is, StorageChannel chan) {

    }

    @Override
    public int getStatusForCell(ItemStack is, IMEInventory handler) {
        return 0;
    }

    @Override
    public double cellIdleDrain(ItemStack is, IMEInventory handler) {
        return 0;
    }
}
