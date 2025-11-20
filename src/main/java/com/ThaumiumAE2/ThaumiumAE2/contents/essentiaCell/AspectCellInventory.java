package com.ThaumiumAE2.ThaumiumAE2.contents.essentiaCell;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.ThaumiumAE2.api.ITAE2EssentiaStack;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.ICellCacheRegistry;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.StorageChannel;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class AspectCellInventory implements IMEInventoryHandler<ITAE2EssentiaStack>, ICellCacheRegistry {

    // static public int MAX_BYTES = 16000;
    // static public int MAX_TYPES = 16;
    static public long ESSENTIA_PER_BYTE = 2;
    private ISaveProvider saveProvider;

    public AspectCellInventory(ItemStack cell, ISaveProvider saveProvider, long bytesCapacity, long typesCapacity) {
        this.saveProvider = saveProvider;
        if (!cell.hasTagCompound()) {
            cell.setTagCompound(new NBTTagCompound());
        }
        data = cell.getTagCompound();

        this.bytesTotal = bytesCapacity;
        this.typesTotal = typesCapacity;

        this.updateFromNBT();
    }

    private void updateFromNBT() {
        if (this.data.hasKey("aspects")) {
            aspects.readFromNBT(this.data, "aspects");
        }
    }

    private void updateToNBT() {
        this.aspects.writeToNBT(data, "aspects");
        if (this.saveProvider != null) {
            this.saveProvider.saveChanges(this);
        }
    }

    private AspectList aspects;
    private NBTTagCompound data;
    private boolean hasVoiding = false;

    private long bytesTotal = 0;
    private long typesTotal = 0;

    private long bytesUsed = 0;
    private long typesUsed = 0;

    // https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/blob/master/src/main/java/appeng/api/storage/ICellCacheRegistry.java
    @Override
    public boolean canGetInv() {
        return true;
    }

    @Override
    public long getTotalBytes() {
        return bytesTotal;
    }

    @Override
    public long getFreeBytes() {
        return bytesTotal - bytesUsed;
    }

    @Override
    public long getUsedBytes() {
        return bytesUsed;
    }

    @Override
    public long getTotalTypes() {
        return typesTotal;
    }

    @Override
    public long getFreeTypes() {
        return typesTotal - typesUsed;
    }

    @Override
    public long getUsedTypes() {
        return typesUsed;
    }

    private boolean isPreformatted() {
        return false;
    }

    @Override
    public int getCellStatus() {

        if (this.bytesUsed == this.bytesTotal) {
            return 4;
        }

        if (this.isPreformatted() || this.typesUsed == this.typesTotal) {
            return 3;
        }

        if (this.bytesUsed == 0 && this.typesUsed == 0) {
            return 1;
        }
        return 2;
    }

    @Override
    public TYPE getCellType() {
        return TYPE.ESSENTIA;
    }

    // https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/blob/master/src/main/java/appeng/api/storage/IMEInventoryHandler.java

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(ITAE2EssentiaStack input) {
        return false;
    }

    @Override
    public boolean canAccept(ITAE2EssentiaStack input) {
        Aspect aspect = input.getAspect();
        long size = input.getStackSize();

        return this.canAcceptAspect(aspect) && this.canAcceptAmount(size);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(int i) {
        return false;
    }

    // https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/blob/master/src/main/java/appeng/api/storage/IMEInventory.java
    @Override
    public ITAE2EssentiaStack injectItems(ITAE2EssentiaStack input, Actionable actionType, BaseActionSource src) {
        // returns the number of items not added.
        final Aspect aspect = input.getAspect();
        final long size = input.getStackSize();
        if (!this.canAcceptAspect(aspect)) {
            return input;
        }

        final long maxEssentia = bytesTotal * ESSENTIA_PER_BYTE;
        final long essentiaCanBeInserted = maxEssentia - this.aspects.visSize();

        // min(size, essentiaCanBeInserted)
        final int toInsert = Math.toIntExact(Math.min(size, essentiaCanBeInserted));

        // !Actionable.SIMULATE
        if (actionType == Actionable.MODULATE) {
            this.aspects.add(aspect, toInsert);
            this.recalculateUsage();
        }

        if (!hasVoiding) {
            input.setStackSize(size - toInsert);
        } else {
            input.setStackSize(0);
        }
        return input;
    }

    @Override
    public ITAE2EssentiaStack extractItems(ITAE2EssentiaStack request, Actionable actionType, BaseActionSource src) {
        final Aspect aspect = request.getAspect();
        final int size = (int) request.getStackSize();

        final int toExtract = Math.min(size, this.aspects.getAmount(aspect));

        // !Actionable.SIMULATE
        if (actionType == Actionable.MODULATE) {
            this.aspects.remove(aspect, toExtract);
            this.recalculateUsage();
        }
        request.setStackSize(toExtract);

        return request;
    }

    @Override
    public StorageChannel getChannel() {
        return TAE2.ESSENTIA_STORAGE;
    }

    @Override
    public ITAE2EssentiaStack getAvailableItem(@NotNull ITAE2EssentiaStack request, int iteration) {
        if (this.aspects.getAmount(request.getAspect()) > 0)
            return request;// or            return new EssentiaStack(request.getAspect(), this.aspects.getAmount(request.getAspect()));
        return null;
    }

    // extension
    public void recalculateUsage() {
        typesUsed = this.aspects.size();
        bytesUsed = this.aspects.visSize() / ESSENTIA_PER_BYTE;

        // напринти usedTypes + usedBytes

        this.updateToNBT();
    }

    // TODO: implement preformatting
    public boolean canAcceptAspect(Aspect aspect) {
        if (this.aspects.getAmount(aspect) == 0) {
            return typesUsed < typesTotal;
        }

        return true;
    }


    public boolean canAcceptAmount(long amount) {
        return (this.aspects.visSize() + amount) / ESSENTIA_PER_BYTE < bytesTotal;
    }
}
