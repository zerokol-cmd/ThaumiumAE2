package com.ThaumiumAE2.ThaumiumAE2.implementation.gui;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.value.sync.ValueSyncHandler;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EssentiaGridWidgetSyncHandler extends ValueSyncHandler<List<EssentiaStack>> {

    @Nullable
    private List<EssentiaStack> cache = new ArrayList<>();

    @Nullable
    private Supplier<List<EssentiaStack>> getter;

    @Nullable
    private Consumer<List<EssentiaStack>> updateCallback;

    /**
     * Constructor with supplier for server-side data
     * @param serverGetter Supplier that provides essentia list from server (e.g., from essentiaNetwork)
     */
    public EssentiaGridWidgetSyncHandler(Supplier<List<EssentiaStack>> serverGetter) {
        if (serverGetter == null) {
            throw new NullPointerException("Server getter must not be null!");
        }

        // Only server needs the getter, client uses cache
        if (NetworkUtils.isClient()) {
            this.getter = null;
        } else {
            this.getter = serverGetter;
        }
    }

    /**
     * Default constructor for client-side initialization
     */
    public EssentiaGridWidgetSyncHandler() {
        this.getter = null;
        this.cache = new ArrayList<>();
    }

    /**
     * Set a callback to be notified when the grid data changes
     * This is used by EssentiaGridWidget to know when to update
     */
    public void setUpdateCallback(Consumer<List<EssentiaStack>> callback) {
        this.updateCallback = callback;
    }

    /**
     * Get the current cached list of essentia stacks
     * @return a list of available essentia
     */
    @Override
    public List<EssentiaStack> getValue() {
        return cache != null ? cache : new ArrayList<>();
    }

    /**
     * Set the cached value
     * @param value new list of essentia stacks
     * @param setSource whether to update the source
     * @param sync whether to sync to remote side
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
     * Called every tick on server to check if data changed
     * @param isFirstSync true if it's the first tick in the ui
     * @return true if the value changed and needs to be synced
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
            return true; // Signal that sync is needed
        }

        return false; // No change
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

            // Compare aspect and amount
            if (!stack1.getAspect().equals(stack2.getAspect())) return false;
            if (stack1.getStackSize() != stack2.getStackSize()) return false;
        }

        return true;
    }

    /**
     * Called when value changes - triggers the update callback
     */
    @Override
    public void notifyUpdate() {
        if (updateCallback != null) {
            updateCallback.accept(this.cache);
        }
    }

    /**
     * Write the essentia list to packet buffer for network sync
     * @param buffer buffer to write to
     * @throws IOException if write fails
     */
    @Override
    public void write(PacketBuffer buffer) throws IOException {
        if (cache == null || cache.isEmpty()) {
            buffer.writeInt(0); // Write size 0
        } else {
            buffer.writeInt(cache.size()); // Write list size

            for (EssentiaStack stack : cache) {
                if (stack == null) {
                    buffer.writeBoolean(false); // Null marker
                } else {
                    buffer.writeBoolean(true); // Non-null marker
                    stack.writeToPacket(buffer); // Write stack data
                }
            }
        }
    }

    /**
     * Read the essentia list from packet buffer
     * @param buffer buffer to read from
     * @throws IOException if read fails
     */
    @Override
    public void read(PacketBuffer buffer) throws IOException {
        int size = buffer.readInt(); // Read list size

        List<EssentiaStack> newList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            boolean hasStack = buffer.readBoolean(); // Check null marker
            if (hasStack) {
                EssentiaStack stack = EssentiaStack.fromPacket(buffer); // Read stack data
                newList.add(stack);
            } else {
                newList.add(null);
            }
        }

        this.cache = newList;
        onValueChanged();

        // Trigger update callback after receiving data from network
        if (updateCallback != null) {
            updateCallback.accept(this.cache);
        }
    }
}
