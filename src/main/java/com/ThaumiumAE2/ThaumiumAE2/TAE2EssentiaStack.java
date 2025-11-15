package com.ThaumiumAE2.ThaumiumAE2;
// спизжено из https://github.com/Nividica/ThaumicEnergistics/blob/AE2-RV6/src/main/java/thaumicenergistics/integration/appeng/TAE2AspectStack.java


import appeng.api.config.FuzzyMode;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAETagCompound;
import com.ThaumiumAE2.api.ITAE2EssentiaStack;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;

import java.io.IOException;


/**
 * @author BrockWS
 */
public class TAE2EssentiaStack implements ITAE2EssentiaStack, Comparable<TAE2EssentiaStack> {

    @Override
    public int compareTo(@NotNull TAE2EssentiaStack o) {
        return 0;
    }

    @Override
    public void add(ITAE2EssentiaStack option) {

    }

    @Override
    public Aspect getAspect() {
        return Aspect.AIR;
    }

    @Override
    public long getStackSize() {
        return 0;
    }

    @Override
    public ITAE2EssentiaStack setStackSize(long stackSize) {
        return null;
    }

    @Override
    public long getCountRequestable() {
        return 0;
    }

    @Override
    public ITAE2EssentiaStack setCountRequestable(long countRequestable) {
        return null;
    }

    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public ITAE2EssentiaStack setCraftable(boolean isCraftable) {
        return null;
    }

    @Override
    public ITAE2EssentiaStack reset() {
        return null;
    }

    @Override
    public boolean isMeaningful() {
        return false;
    }

    @Override
    public void incStackSize(long i) {

    }

    @Override
    public void decStackSize(long i) {

    }

    @Override
    public void incCountRequestable(long i) {

    }

    @Override
    public void decCountRequestable(long i) {

    }

    @Override
    public void writeToNBT(NBTTagCompound i) {

    }

    @Override
    public boolean fuzzyComparison(Object st, FuzzyMode mode) {
        return false;
    }

    @Override
    public void writeToPacket(ByteBuf data) throws IOException {

    }

    @Override
    public ITAE2EssentiaStack copy() {
        return null;
    }

    @Override
    public ITAE2EssentiaStack empty() {
        return null;
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
        return null;
    }

    @Override
    public String getLocalizedName() {
        return "";
    }
}
