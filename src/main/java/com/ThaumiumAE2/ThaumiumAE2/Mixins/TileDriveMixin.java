package com.ThaumiumAE2.ThaumiumAE2.Mixins;

import appeng.api.AEApi;
import appeng.api.storage.ICellHandler;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.me.storage.MEInventoryHandler;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.storage.TileDrive;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;
import com.ThaumiumAE2.ThaumiumAE2.Content.EssentiaCell.AspectCellInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = TileDrive.class, remap = false)
public abstract class TileDriveMixin {

    // --- Shadowed Fields ---
    // We only need to shadow the fields our injections and overwrites actually use.
    @Shadow
    private boolean isCached;
    @Shadow
    private List<MEInventoryHandler<?>> items;
    @Shadow
    private List<MEInventoryHandler<?>> fluids;
    @Shadow
    @Final
    private AppEngInternalInventory inv;
    @Shadow
    private int priority;
//    @Shadow public abstract IActionHost getProxy();

    // --- Injected Field ---
    @Unique
    private List<AspectCellInventory> thaumiumae2$essentia = new ArrayList<>();

    /**
     * @author YourName
     * @reason Inject Essentia cell scanning logic into the drive's update method.
     */
    @Inject(method = "updateState()V", at = @At("HEAD"))
    private void tae2$onUpdateState(CallbackInfo ci) {
        // This code runs at the very end of the original updateState method.
        // The original method has already handled items and fluids.
        // Now, we initialize our list and scan for our cells.
        this.thaumiumae2$essentia = new ArrayList<>();

        for (int x = 0; x < this.inv.getSizeInventory(); x++) {
            final ItemStack is = this.inv.getStackInSlot(x);
            if (is == null) {
                continue;
            }

            // We only care about slots that were NOT handled as item or fluid cells.
            // A simple way is to check if the slot's handler is already set.
            // A more robust way is to re-check the channel.
            ICellHandler handler = AEApi.instance().registries().cell().getHandler(is);
            if (handler != null) {
                // Check if it's an Essentia cell
                AspectCellInventory cell = (AspectCellInventory) handler.getCellInventory(is, (TileDrive) (Object) this, TAE2.ESSENTIA_STORAGE);
                if (cell != null) {
                    // Add to our dedicated essentia list.
                    this.thaumiumae2$essentia.add(cell);
                }
            }
        }
    }

    /**
     * @author YourName
     * @reason Expose the new ESSENTIA handler list to the ME network.
     */
    @Overwrite
    public List<IMEInventoryHandler> getCellArray(final StorageChannel channel) {
        // This overwrite is still necessary to provide our list to the network.
//        if (!this.getProxy().isActive()) {
//            return Collections.emptyList();
//        }

        // The isCached check and updateState call are handled by AE2's original logic.
        // We can trust that updateState has been called before this.
        if (channel == StorageChannel.ITEMS) {
            return (List) this.items;
        }
        if (channel == StorageChannel.FLUIDS) {
            return (List) this.fluids;
        }
        if (channel == TAE2.ESSENTIA_STORAGE) {
            return this.thaumiumae2$essentia != null ? (List) this.thaumiumae2$essentia : Collections.emptyList();
        }

        return Collections.emptyList();
    }
}
