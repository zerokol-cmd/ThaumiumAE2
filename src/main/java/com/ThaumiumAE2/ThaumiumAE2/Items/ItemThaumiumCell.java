package com.ThaumiumAE2.ThaumiumAE2.Items;

import appeng.api.AEApi;
import appeng.api.implementations.tiles.IChestOrDrive;
import appeng.api.storage.*;
import appeng.block.storage.BlockDrive;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import static appeng.core.features.AEFeature.MEDrive;

public class ItemThaumiumCell extends Item implements ICellHandler {
	 private long bytesCapacity;
	 private long typesCapacity;
     public final static String ID = "thaumium_cell";

    public ItemThaumiumCell(long bytesCapacity, long typesCapacity) {
        this.bytesCapacity = bytesCapacity;
        this.typesCapacity = typesCapacity;

		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setHasSubtypes(false);
		
		AEApi.instance().registries().cell().addCellHandler(this);
    }

    @Override
    public boolean isCell(ItemStack itemStack) {
        return itemStack.getItem() == this;
    }

    @Override
    public IMEInventoryHandler getCellInventory(ItemStack is, ISaveProvider saveProvider, StorageChannel channel) {
		if (channel != TAE2.ESSENTIA_STORAGE) {
			return null;
		}

        return new AspectCellInventory( bytesCapacity, typesCapacity);
    }

    @Override
    public IIcon getTopTexture_Light() {
        return null;
    }

    @Override
    public IIcon getTopTexture_Medium() {
        return null;
    }

//        if ((channel != StorageChannel.FLUIDS) || !(essentiaCell.getItem() instanceof ItemThaumiumCell)) {
//            return null;
//        }


    @Override
    public IIcon getTopTexture_Dark() {
        return null;
    }

    @Override
    public void openChestGui(EntityPlayer player, IChestOrDrive chest, ICellHandler cellHandler, IMEInventoryHandler inv, ItemStack is, StorageChannel chan) {

    }

    @Override
    public int getStatusForCell(ItemStack is, IMEInventory handler) {
		if (handler == null) {
			return 0; // invalid
		}

        return ((AspectCellInventory) handler).getCellStatus();
    }

    @Override
    public double cellIdleDrain(ItemStack is, IMEInventory handler) {
        return 0;
    }
}
