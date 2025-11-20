package com.ThaumiumAE2.ThaumiumAE2.implementation.gui;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaNetwork;
import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import com.ThaumiumAE2.api.IEssentiaNetwork;
import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.utils.MouseData;
import com.cleanroommc.modularui.value.sync.ValueSyncHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class EssentiaSlotSyncHandler extends ValueSyncHandler<EssentiaStack> {
    // ... (static methods and constants remain the same)


    @Nullable
    private EssentiaStack cache;

    Supplier<EssentiaStack> getter;
    public EssentiaSlotSyncHandler(Supplier<EssentiaStack> clientGetter, Supplier<EssentiaStack> serverGetter) {
        if (clientGetter == null && serverGetter == null) {
            throw new NullPointerException("Client or server getter must not be null!");
        }
        if (NetworkUtils.isClient()) {
            this.getter = clientGetter != null ? clientGetter : serverGetter;
        } else {
            this.getter = serverGetter != null ? serverGetter : clientGetter;
        }
        this.cache = this.getter.get();
    }

//    public EssentiaSlotSyncHandler() {
//    }


    @Override
    public void setValue(EssentiaStack value, boolean setSource, boolean sync) {
        EssentiaStack essentia = (EssentiaStack) value.copy();
        this.cache = essentia;
        onValueChanged();
    }

    @Nullable
    @Override
    public EssentiaStack getValue() {
        return this.cache;
    }
    // This method now reflects the selected Essentia and its amount in the network

    int i = 0;

    @Override
    public boolean updateCacheFromSource(boolean isFirstSync) {
        if (isFirstSync) {
            setValue(new EssentiaStack(Aspect.AIR, 1));
        }
        EssentiaStack stack = this.getter.get();
        if(stack != cache)
        {
            this.cache = stack;
            return true;
        }
        return false;
    }

    @Override
    public void notifyUpdate() {
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

}
