package com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI;

import com.ThaumiumAE2.ThaumiumAE2.Implementation.EssentiaStack;
import com.ThaumiumAE2.ThaumiumAE2.Interfaces.IEssentiaNetwork;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.utils.MouseData;
import com.cleanroommc.modularui.value.sync.ValueSyncHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.blocks.BlockWarded;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EssentiaGridWidgetSyncHandler extends ValueSyncHandler<List<EssentiaStack>> {

    public static final int SYNC_CLICK = 0;

    @Nullable
    private List<EssentiaStack> cache = new ArrayList<>();

    @Nullable
    private Supplier<List<EssentiaStack>> getter;

    @Nullable
    private IEssentiaNetwork essentiaNetwork;

    @Nullable
    private Consumer<List<EssentiaStack>> updateCallback;

    long fillEssentia(EssentiaStack heldEssentia) {
        if (essentiaNetwork != null) {
            return essentiaNetwork.injectEssentia(
                heldEssentia.getAspect(),
                heldEssentia.getStackSize(),
                false
            );
        }
        return 0;
    }

    long drainEssentia(EssentiaStack stack) {
        if (essentiaNetwork != null) {
            return essentiaNetwork.extractEssentia(
                stack.getAspect(),
                stack.getStackSize(),
                false
            );
        }
        return 0;
    }

    void replaceWithEmptyJar() {
        ItemStack jar = new ItemStack(ConfigBlocks.blockJar);
        getSyncManager().setCursorItem(jar);
    }

    protected static void addItemToPlayerInventory(EntityPlayer player, ItemStack stack) {
        if (stack == null)
            return;
        if (!player.inventory.addItemStackToInventory(stack) && !player.worldObj.isRemote) {
            EntityItem dropItem = player.entityDropItem(stack, 0);
            dropItem.delayBeforeCanPickup = 0;
        }
    }

    void replaceJar(Aspect aspect, long amount, ItemStack container) {
        if (amount < 0 || amount > 64) return;

        if (amount == 0) {
            replaceWithEmptyJar();
            return;
        }

        ItemJarFilled jarItem = (ItemJarFilled) ConfigItems.itemJarFilled;
        AspectList list = new AspectList();
        list.aspects.put(aspect, (int) amount);

        ItemStack filledJar = new ItemStack(jarItem);
        jarItem.setAspects(filledJar, list);

        if (container.stackSize > 1) {
            container.stackSize -= 1;
            getSyncManager().setCursorItem(container);

            addItemToPlayerInventory(getSyncManager().getPlayer(), filledJar);
        } else {

            getSyncManager().setCursorItem(filledJar);
        }
    }

    //issue: we pass here only if slot is not empty
    void tryClickContainer(EssentiaGridSlot.SlotMouseData data) {
        ItemStack heldItem = getSyncManager().getCursorItem();
        if (heldItem == null || heldItem.stackSize == 0) {
            return;
        }

        ItemStack heldItemCopy = heldItem.copy();
        if (heldItemCopy.getItem() instanceof IEssentiaContainerItem container) {
            var aspects = container.getAspects(heldItemCopy);
            if (aspects != null && aspects.aspects != null) {
                aspects.aspects.forEach((aspect, count) -> {
                    long essentiaLeft = fillEssentia(new EssentiaStack(aspect, count));
                    replaceJar(aspect, essentiaLeft, heldItem);
                });
            }
            return;
        }
        if (heldItemCopy.getItem() instanceof BlockJarItem) {
            Aspect aspect = data.aspect;
            long extracted = drainEssentia(new EssentiaStack(aspect, 64));
            replaceJar(aspect, extracted, heldItem);
        }
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) {
        if (id == SYNC_CLICK) {
            tryClickContainer(EssentiaGridSlot.SlotMouseData.readPacket(buf));
        }
    }

    /**
     * Constructor with essentia network reference
     * Network can be null on client side
     */
    public EssentiaGridWidgetSyncHandler(@Nullable IEssentiaNetwork essentiaNetwork) {
        // On client, essentiaNetwork will be null - this is expected
        if (!NetworkUtils.isClient() && essentiaNetwork != null) {
            // Server side - store network and create getter
            this.essentiaNetwork = essentiaNetwork;
            this.getter = () -> {
                if (this.essentiaNetwork != null) {
                    return this.essentiaNetwork.getStoredEssentia();
                }
                return new ArrayList<>();
            };
        } else {
            // Client side - no network access needed
            this.essentiaNetwork = null;
            this.getter = null;
        }
    }

    /**
     * Default constructor for client-side initialization
     */
    public EssentiaGridWidgetSyncHandler() {
        this(null);
    }

    /**
     * Set a callback to be notified when the grid data changes
     */
    public void setUpdateCallback(Consumer<List<EssentiaStack>> callback) {
        this.updateCallback = callback;
    }

    /**
     * Get the current cached list of essentia stacks
     */
    @Override
    public List<EssentiaStack> getValue() {
        return cache != null ? cache : new ArrayList<>();
    }

    /**
     * Set the cached value
     */
    @Override
    public void setValue(List<EssentiaStack> value, boolean setSource, boolean sync) {
        if (value == null) {
            this.cache = new ArrayList<>();
        } else {
            // Deep copy to avoid reference issues
            this.cache = new ArrayList<>();
            for (EssentiaStack stack : value) {
                this.cache.add((EssentiaStack) stack.copy());
            }
        }
        onValueChanged();

        // Trigger update callback
        if (updateCallback != null) {
            updateCallback.accept(this.cache);
        }
    }

    /**
     * Update cache from source (server-side)
     */
    @Override
    public boolean updateCacheFromSource(boolean isFirstSync) {
        // Only server updates from source
        if (getter == null) {
            return false;
        }

        List<EssentiaStack> newList = getter.get();

        if (newList == null) {
            newList = new ArrayList<>();
        }

        // Check if the list changed
        if (isFirstSync || !listsEqual(cache, newList)) {
            // Deep copy the new list
            this.cache = new ArrayList<>();
            for (EssentiaStack stack : newList) {
                this.cache.add((EssentiaStack) stack.copy());
            }
            return true;
        }

        return false;
    }

    /**
     * Compare two lists of EssentiaStack for equality
     */
    private boolean listsEqual(List<EssentiaStack> list1, List<EssentiaStack> list2) {
        if (list1 == null && list2 == null) return true;
        if (list1 == null || list2 == null) return false;
        if (list1.size() != list2.size()) return false;

        for (int i = 0; i < list1.size(); i++) {
            EssentiaStack stack1 = list1.get(i);
            EssentiaStack stack2 = list2.get(i);

            if (stack1 == null && stack2 == null) continue;
            if (stack1 == null || stack2 == null) return false;

            if (!stack1.getAspect().equals(stack2.getAspect())) return false;
            if (stack1.getStackSize() != stack2.getStackSize()) return false;
        }

        return true;
    }

    /**
     * Called when value changes
     */
    @Override
    public void notifyUpdate() {
        if (updateCallback != null) {
            updateCallback.accept(this.cache);
        }
    }

    /**
     * Write the essentia list to packet buffer
     */
    @Override
    public void write(PacketBuffer buffer) throws IOException {
        if (cache == null || cache.isEmpty()) {
            buffer.writeInt(0);
        } else {
            buffer.writeInt(cache.size());

            for (EssentiaStack stack : cache) {
                if (stack == null) {
                    buffer.writeBoolean(false);
                } else {
                    buffer.writeBoolean(true);
                    stack.writeToPacket(buffer);
                }
            }
        }
    }

    /**
     * Read the essentia list from packet buffer
     */
    @Override
    public void read(PacketBuffer buffer) throws IOException {
        int size = buffer.readInt();

        List<EssentiaStack> newList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            boolean hasStack = buffer.readBoolean();
            if (hasStack) {
                EssentiaStack stack = EssentiaStack.fromPacket(buffer);
                newList.add(stack);
            } else {
                newList.add(null);
            }
        }

        this.cache = newList;
        onValueChanged();

        if (updateCallback != null) {
            updateCallback.accept(this.cache);
        }
    }
}
