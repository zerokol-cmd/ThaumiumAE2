package com.ThaumiumAE2.ThaumiumAE2.Items.Cells;

import appeng.api.config.FuzzyMode;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAETagCompound;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.ThaumiumAE2.api.ITAE2EssentiaStack;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;

import java.io.IOException;
import java.nio.charset.StandardCharsets; // Requires Java 7/8. If not, use java.nio.charset.Charset.forName("UTF-8")
import java.util.Objects;

public class EssentiaStack extends Item implements ITAE2EssentiaStack, Comparable<EssentiaStack> {

    private Aspect aspect;
    private long stackSize = 0;
    private long countRequestable = 0;
    private boolean isCraftable = false;

    // --- NBT Keys ---
    private static final String NBT_KEY_ASPECT = "aspect";
    private static final String NBT_KEY_AMOUNT = "amount";

    // --- Constructors ---

    public EssentiaStack(Aspect aspect, long stackSize) {
        this.aspect = aspect;
        this.stackSize = stackSize;
    }

    private EssentiaStack(EssentiaStack other) {
        this.aspect = other.aspect;
        this.stackSize = other.stackSize;
        this.countRequestable = other.countRequestable;
        this.isCraftable = other.isCraftable;
    }

    // --- Static Factory Methods ---

    /**
     * Creates an EssentiaStack from an NBT compound tag.
     */
    public static EssentiaStack fromNBT(NBTTagCompound tag) {
        if (tag == null || !tag.hasKey(NBT_KEY_ASPECT))
            return null;

        Aspect aspect = Aspect.getAspect(tag.getString(NBT_KEY_ASPECT));
        if (aspect == null)
            return null;

        long amount = tag.getLong(NBT_KEY_AMOUNT);
        return new EssentiaStack(aspect, amount);
    }

    /**
     * Creates an EssentiaStack from a network packet buffer.
     * Uses length-prefixed string reading common in older netty/forge.
     */
    public static EssentiaStack fromPacket(ByteBuf data) throws IOException {
        // Read the length of the string (short, 2 bytes)
        short length = data.readShort();

        // Read the bytes of the string
        byte[] bytes = new byte[length];
        data.readBytes(bytes);

        // Convert bytes to string (UTF-8 encoding)
        String aspectTag = new String(bytes, StandardCharsets.UTF_8);

        // Read the stack size
        long amount = data.readLong();

        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null)
            return null;
        return new EssentiaStack(aspect, amount);
    }

    // --- Comparison and Hashing ---

    @Override
    public int compareTo(@NotNull EssentiaStack other) {
        return this.aspect.getTag().compareTo(other.aspect.getTag());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EssentiaStack that = (EssentiaStack) obj;
        // Standard AE2 stack equality ignores stack size
        return Objects.equals(aspect, that.aspect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aspect);
    }

    // --- ITAE2EssentiaStack Implementation ---

    @Override
    public void add(ITAE2EssentiaStack option) {
        if (option != null && this.getAspect().equals(option.getAspect())) {
            this.stackSize += option.getStackSize();
        }
    }

    @Override
    public Aspect getAspect() {
        return this.aspect;
    }

    @Override
    public long getStackSize() {
        return this.stackSize;
    }

    @Override
    public ITAE2EssentiaStack setStackSize(long stackSize) {
        this.stackSize = stackSize;
        return this;
    }

    @Override
    public long getCountRequestable() {
        return this.countRequestable;
    }

    @Override
    public ITAE2EssentiaStack setCountRequestable(long countRequestable) {
        this.countRequestable = countRequestable;
        return this;
    }

    @Override
    public boolean isCraftable() {
        return this.isCraftable;
    }

    @Override
    public ITAE2EssentiaStack setCraftable(boolean isCraftable) {
        this.isCraftable = isCraftable;
        return this;
    }

    @Override
    public ITAE2EssentiaStack reset() {
        this.countRequestable = 0;
        this.isCraftable = false;
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return this.aspect != null && this.stackSize > 0;
    }

    @Override
    public void incStackSize(long i) {
        this.stackSize += i;
    }

    @Override
    public void decStackSize(long i) {
        this.stackSize -= i;
    }

    @Override
    public void incCountRequestable(long i) {
        this.countRequestable += i;
    }

    @Override
    public void decCountRequestable(long i) {
        this.countRequestable -= i;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setString(NBT_KEY_ASPECT, this.aspect.getTag());
        tag.setLong(NBT_KEY_AMOUNT, this.stackSize);
    }

    @Override
    public boolean fuzzyComparison(Object st, FuzzyMode mode) {
        // Essentia has no NBT data to compare, so fuzzy is the same as equals
        if (st instanceof EssentiaStack) {
            return this.equals(st);
        }
        return false;
    }

    @Override
    public void writeToPacket(ByteBuf data) throws IOException {
        // Convert string to bytes
        byte[] bytes = this.aspect.getTag().getBytes(StandardCharsets.UTF_8);

        // Write the length (short, 2 bytes)
        data.writeShort(bytes.length);

        // Write the bytes
        data.writeBytes(bytes);

        // Write the stack size
        data.writeLong(this.stackSize);
    }

    @Override
    public ITAE2EssentiaStack copy() {
        return new EssentiaStack(this);
    }

    @Override
    public ITAE2EssentiaStack empty() {
        return new EssentiaStack(this.aspect, 0);
    }

    @Override
    public IAETagCompound getTagCompound() {
        // Essentia stacks don't have NBT data like ItemStacks
        return null;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public boolean isFluid() {
        return false;
    }

    @Override
    public StorageChannel getChannel() {
        return TAE2.ESSENTIA_STORAGE;
    }

    @Override
    public String getLocalizedName() {
        return this.aspect.getName();
    }
}
