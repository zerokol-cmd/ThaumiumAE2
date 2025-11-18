package com.ThaumiumAE2.ThaumiumAE2.implementation.gui;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import com.ThaumiumAE2.api.IEssentiaNetwork;
import com.cleanroommc.modularui.utils.MouseData;
import com.cleanroommc.modularui.value.sync.ValueSyncHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import java.io.IOException;

public class EssentiaSlotSyncHandler extends ValueSyncHandler<EssentiaStack> {
    // ... (static methods and constants remain the same)

    @Nullable
    private EssentiaStack selectedEssentia; // Represents the currently selected Essentia in the terminal
    private final IEssentiaNetwork essentiaNetwork; // The AE2 network interface

    @Nullable
    private EssentiaStack cache;
    private boolean canFillSlot = true, canDrainSlot = true, controlsAmount = true, phantom = false;

    public EssentiaSlotSyncHandler(IEssentiaNetwork essentiaNetwork) {
        this.essentiaNetwork = essentiaNetwork;
    }

    @Override
    public void setValue(EssentiaStack value, boolean setSource, boolean sync) {

    }

    // This method now reflects the selected Essentia and its amount in the network
    @Override
    public boolean updateCacheFromSource(boolean isFirstSync) {
        if (selectedEssentia == null) {
            setValue(null, false, false);
            return true;
        }

        long amount = essentiaNetwork.getEssentiaAmount(selectedEssentia.getAspect());
        if (this.cache == null || this.cache.getStackSize() != amount) {
            setValue(new EssentiaStack(selectedEssentia.getAspect(), amount), false, false);
            return true;
        }
        return false;
    }

    @Override
    public void notifyUpdate() {
        if (this.selectedEssentia == null) {
            setValue(null, false, true);
            return;
        }
        EssentiaStack current = new EssentiaStack(this.selectedEssentia.getAspect(), this.essentiaNetwork.getEssentiaAmount(this.selectedEssentia.getAspect()));
        setValue(current, false, true);
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        if (this.cache == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            this.cache.writeToPacket(buffer);
        }
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        if (buffer.readBoolean()) {
            // On the client, we just update the cache. The server is the authority.
            this.cache = EssentiaStack.fromPacket(buffer);
        } else {
            this.cache = null;
        }
        onValueChanged();
    }

    // Method to set the selected Essentia from the terminal's UI
    public void setSelectedEssentia(@Nullable Aspect aspect) {
        if (aspect == null) {
            this.selectedEssentia = null;
        } else {
            // The amount will be fetched from the network.
            this.selectedEssentia = new EssentiaStack(aspect, 0);
        }
        // Force a sync with the server to get the correct amount.
        updateCacheFromSource(true);
    }

    private ItemStack fillEssentiaContainer(EssentiaStack essentia, ItemStack container) {
// Thaumcraft phial logic: If container is empty phial, fill it with 10 essentia (adjust per mod)
        if (container.getItem() instanceof IEssentiaContainerItem) {
            IEssentiaContainerItem cont = (IEssentiaContainerItem) container.getItem();
            NBTTagCompound tag = container.getTagCompound();
            if (tag == null || tag.getInteger("amount") == 0) {
                if (essentia.getStackSize() >= 10) {
                    ItemStack filled = container.copy();
                    NBTTagCompound newTag = new NBTTagCompound();
                    newTag.setString("aspect", essentia.getAspect().getTag());
                    newTag.setInteger("amount", 10);
                    filled.setTagCompound(newTag);
                    essentia.decStackSize(10);
                    return filled;
                }
            }
        }
        return null;
    }

    protected void drainEssentia(boolean processFullStack) {
        ItemStack heldItem = getSyncManager().getCursorItem();
        if (heldItem == null || heldItem.stackSize == 0 || this.selectedEssentia == null) {
            return;
        }
        ItemStack heldItemSizedOne = heldItem.copy();
        heldItemSizedOne.stackSize = 1;

        long networkAmount = this.essentiaNetwork.getEssentiaAmount(this.selectedEssentia.getAspect());
        if (networkAmount <= 0) {
            return;
        }

        // Create a temporary stack to simulate the filling process
        EssentiaStack essentiaToDrain = new EssentiaStack(this.selectedEssentia.getAspect(), networkAmount);
        ItemStack filledContainer = fillEssentiaContainer(essentiaToDrain, heldItemSizedOne);

        if (filledContainer != null) {
            long filledAmount = networkAmount - essentiaToDrain.getStackSize();
            if (filledAmount < 1) {
                return;
            }

            // Actually extract from the network
            long extractedAmount = this.essentiaNetwork.extractEssentia(this.selectedEssentia.getAspect(), filledAmount, false);
            if (extractedAmount < filledAmount) {
                // Should not happen if logic is correct, but as a safeguard
                return;
            }

            if (processFullStack) {
                long availableAmount = this.essentiaNetwork.getEssentiaAmount(this.selectedEssentia.getAspect());
                long additionalParallel = Math.min(heldItem.stackSize - 1, availableAmount / filledAmount);
                if (additionalParallel > 0) {
                    this.essentiaNetwork.extractEssentia(this.selectedEssentia.getAspect(), filledAmount * additionalParallel, false);
                    filledContainer.stackSize += additionalParallel;
                }
            }
            replaceCursorItemStack(filledContainer);
            playSound(this.selectedEssentia, false);
        }
    }

    private void playSound(EssentiaStack essentia, boolean fill) {
        // Optional: Play Thaumcraft sound if desired
    }

    protected void replaceCursorItemStack(ItemStack resultStack) {
        EntityPlayer player = getSyncManager().getPlayer();
        int resultStackMaxStackSize = resultStack.getMaxStackSize();
        while (resultStack.stackSize > resultStackMaxStackSize) {
            player.inventory.getItemStack().stackSize -= resultStackMaxStackSize;
            addItemToPlayerInventory(player, resultStack.splitStack(resultStackMaxStackSize));
        }
        if (getSyncManager().getCursorItem().stackSize == resultStack.stackSize) {
            getSyncManager().setCursorItem(resultStack);
        } else {
            ItemStack heldItem = getSyncManager().getCursorItem();
            heldItem.stackSize -= resultStack.stackSize;
            addItemToPlayerInventory(player, resultStack);
        }
    }
    protected static void addItemToPlayerInventory(EntityPlayer player, ItemStack stack) {
        if (stack == null)
            return;
        if (!player.inventory.addItemStackToInventory(stack) && !player.worldObj.isRemote) {
            EntityItem dropItem = player.entityDropItem(stack, 0);
            dropItem.delayBeforeCanPickup = 0;
        }
    }

    protected void fillEssentia(@NotNull EssentiaStack heldEssentia, boolean processFullStack) {
        ItemStack heldItem = getSyncManager().getCursorItem();
        if (heldItem == null || heldItem.stackSize == 0) {
            return;
        }
        ItemStack heldItemSizedOne = heldItem.copy();
        heldItemSizedOne.stackSize = 1;

        // Check if the network can accept this type of essentia. This might involve checking filters.
        // For simplicity, we assume it can.

        ItemStack itemStackEmptied = getContainerForFilledItem(heldItemSizedOne);
        if (itemStackEmptied == null) {
            return;
        }

        long essentiaAmountTaken = heldEssentia.getStackSize();
        long parallel = processFullStack ? heldItem.stackSize : 1;

        EssentiaStack copiedEssentiaStack = (EssentiaStack) heldEssentia.copy();
        copiedEssentiaStack.setStackSize(essentiaAmountTaken * parallel);

        // Inject into the network
        long injectedAmount = this.essentiaNetwork.injectEssentia(copiedEssentiaStack.getAspect(), copiedEssentiaStack.getStackSize(), false);

        if (injectedAmount > 0) {
            itemStackEmptied.stackSize = (int) (injectedAmount / essentiaAmountTaken);
            replaceCursorItemStack(itemStackEmptied);
            playSound(heldEssentia, true);
        }
    }
    private ItemStack getContainerForFilledItem(ItemStack filledItem) {
        if (filledItem.getItem() instanceof IEssentiaContainerItem) {
            IEssentiaContainerItem cont = (IEssentiaContainerItem) filledItem.getItem();
            NBTTagCompound tag = filledItem.getTagCompound();
            if (tag != null && tag.getInteger("amount") > 0) {
                ItemStack empty = filledItem.copy();
                empty.setTagCompound(null); // Clear NBT for empty phial
                return empty;
            }
        }
        return null;
    }

    // Other methods like tryClickPhantom will also need to be adapted to use the network interface
    private void tryClickPhantom(MouseData mouseData, ItemStack cursorStack) {
        if (mouseData.mouseButton == 0) { // Left-click to take Essentia
            if (selectedEssentia != null) {
                long amountToExtract = mouseData.shift ? Long.MAX_VALUE : 1000;
                essentiaNetwork.extractEssentia(selectedEssentia.getAspect(), amountToExtract, false);
            }
        } else if (mouseData.mouseButton == 1) { // Right-click to add Essentia (if in creative/phantom mode)
            if (selectedEssentia != null) {
                long amountToInject = 1000;
                essentiaNetwork.injectEssentia(selectedEssentia.getAspect(), amountToInject, false);
            }
        }
    }

    @Nullable
    @Override
    public EssentiaStack getValue() {
        return this.cache;
    }
}
