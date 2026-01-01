package com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI;

import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.utils.MouseData;
import com.cleanroommc.modularui.utils.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.aspects.Aspect;

import java.io.IOException;

public class EssentiaGridSlot extends EssentiaSlot {
    EssentiaGridWidget grid;
    int slotId = 0;

    EssentiaGridSlot(EssentiaGridWidget grid, int id) {
        this.grid = grid;
        this.slotId = id;
    }

    static class SlotMouseData extends MouseData {
        Aspect aspect;

        public void writeToPacket(PacketBuffer buffer) {
            super.writeToPacket(buffer);
            try {
                buffer.writeStringToBuffer(this.aspect.getTag());
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }

        }

        public static SlotMouseData readPacket(PacketBuffer buffer) {
            int button = buffer.readVarIntFromBuffer();
            byte data = buffer.readByte();
            Aspect slotAspect = Aspect.AIR;
            try {
                slotAspect = Aspect.aspects.get(buffer.readStringFromBuffer(80));
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
            return new SlotMouseData(slotAspect, Side.SERVER, button, (data & 1) != 0, (data & 2) != 0, (data & 4) != 0);
        }

        @SideOnly(Side.CLIENT)
        public static SlotMouseData create(Aspect slotAspect, int mouse) {
            return new SlotMouseData(slotAspect, Side.CLIENT, mouse, Interactable.hasShiftDown(), Interactable.hasControlDown(), Interactable.hasAltDown());
        }

        public SlotMouseData(Aspect slotAspect, Side side, int mouseButton, boolean shift, boolean ctrl, boolean alt) {
            super(side, mouseButton, shift, ctrl, alt);
            this.aspect = slotAspect;
        }
    }

    public @NotNull Result onMousePressed(int mouseButton) {
        if (!this.syncHandler.canFill() && !this.syncHandler.canDrain()) {
            return Result.ACCEPT;
        }
        if (this.syncHandler.getValue() == null)
            return Result.ACCEPT;

        ItemStack cursorStack = Platform.getClientPlayer().inventory.getItemStack();

        if (cursorStack != null) {
            SlotMouseData mouseData = SlotMouseData.create(this.syncHandler.getValue().getAspect(), mouseButton);
            grid.getSyncHandler().syncToServer(EssentiaGridWidgetSyncHandler.SYNC_CLICK, mouseData::writeToPacket);
            return Result.ACCEPT;
        }
        return Result.SUCCESS;
    }

}
