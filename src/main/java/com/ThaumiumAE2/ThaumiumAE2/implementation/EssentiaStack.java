package com.ThaumiumAE2.ThaumiumAE2.implementation;


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

public class EssentiaStack implements ITAE2EssentiaStack, Comparable<EssentiaStack> {
    private final Aspect aspect;
    private long stackSize;
    private long countRequestable = 0;
    private boolean isCraftable = false;

    private static final String NBT_KEY_ASPECT = "aspect";
    private static final String NBT_KEY_AMOUNT = "amount";

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

    public static EssentiaStack fromNBT(NBTTagCompound tag) {
        if (tag == null || !tag.hasKey(NBT_KEY_ASPECT))
            return null;

        Aspect aspect = Aspect.getAspect(tag.getString(NBT_KEY_ASPECT));
        if (aspect == null)
            return null;

        long amount = tag.getLong(NBT_KEY_AMOUNT);
        return new EssentiaStack(aspect, amount);
    }

    public static EssentiaStack fromPacket(ByteBuf data) throws IOException {
        short length = data.readShort();
        byte[] bytes = new byte[length];
        data.readBytes(bytes);
        String aspectTag = new String(bytes, StandardCharsets.UTF_8);

        long amount = data.readLong();

        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null)
            return null;
        return new EssentiaStack(aspect, amount);
    }

    @Override
    public void add(ITAE2EssentiaStack option) {
        if (option != null && option.getAspect() == aspect) {
            this.stackSize += option.getStackSize();
        }
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
    public boolean fuzzyComparison(Object o, FuzzyMode mode) {
        return o instanceof EssentiaStack ? this.equals(o) : false;
    }

    @Override
    public void writeToPacket(ByteBuf data) throws IOException {
        byte[] bytes = this.aspect.getTag().getBytes(StandardCharsets.UTF_8);

        data.writeShort(bytes.length);
        data.writeBytes(bytes);
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

    @Override
    public Aspect getAspect() {
        return this.aspect;
    }

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

}
