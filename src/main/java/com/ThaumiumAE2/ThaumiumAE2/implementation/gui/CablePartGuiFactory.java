package com.ThaumiumAE2.ThaumiumAE2.implementation.gui;

import appeng.tile.networking.TileCableBus;
import com.ThaumiumAE2.ThaumiumAE2.contents.terminals.PartEssentiaTerminal;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;

public class CablePartGuiFactory extends AbstractUIFactory<SidedPosGuiData> {

    public static final CablePartGuiFactory INSTANCE = new CablePartGuiFactory("gregtech:cover");
    protected CablePartGuiFactory(String name) {
        super(name);
    }
    public void open(EntityPlayerMP player,  TileEntity tile, ForgeDirection side) {

        SidedPosGuiData guiData = new SidedPosGuiData(
            player,
            tile.xCoord,
            tile.yCoord,
            tile.zCoord,
            side);
        GuiManager.open(this, guiData, player);
    }

    @NotNull
    @Override
    public IGuiHolder<SidedPosGuiData> getGuiHolder(SidedPosGuiData data) {
        TileEntity tileEntity = data.getTileEntity();
        var side = data.getSide();
        if (tileEntity instanceof TileCableBus bus) {
            var part = bus.getPart(side);
            if (part instanceof PartEssentiaTerminal terminal) {
                return ((IGuiHolder<SidedPosGuiData>) terminal.getGuiHolder());
            }
        }

        return null;
    }

    @Override
    public void writeGuiData(SidedPosGuiData guiData, PacketBuffer buffer) {
        buffer.writeVarIntToBuffer(guiData.getX());
        buffer.writeVarIntToBuffer(guiData.getY());
        buffer.writeVarIntToBuffer(guiData.getZ());
        buffer.writeByte(
            guiData.getSide()
                .ordinal());
    }

    @NotNull
    @Override
    public SidedPosGuiData readGuiData(EntityPlayer player, PacketBuffer buffer) {
        return new SidedPosGuiData(
            player,
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            buffer.readVarIntFromBuffer(),
            ForgeDirection.getOrientation(buffer.readByte()));
    }
}
