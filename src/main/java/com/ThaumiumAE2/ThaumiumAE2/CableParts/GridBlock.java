package com.ThaumiumAE2.ThaumiumAE2.CableParts;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
//import com.ThaumiumAE2.ThaumiumAE2.api.grid.IEssentiaGrid;
//import com.ThaumiumAE2.ThaumiumAE2.api.grid.IMEEssentiaMonitor;
import java.util.EnumSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * AE GridBlock used for all ThaumiumAE2 parts.
 * Inspired by AEPartGridBlock from Thaumic Energistics.
 */
public class GridBlock implements IGridBlock {

    /**
     * The part using this grid block.
     */
    protected final CablePartBase part;

    /**
     * Creates the grid block for the specified part.
     *
     * @param part The cable part this grid block belongs to.
     */
    public GridBlock(final CablePartBase part) {
        this.part = part;
    }

    private IGrid getGrid() {
        IGridNode node = this.part.getGridNode(null); // Direction is irrelevant for parts
        return (node != null) ? node.getGrid() : null;
    }

    /**
     * Gets the energy grid from the network.
     *
     * @return The energy grid, or null if not connected.
     */
    public IEnergyGrid getEnergyGrid() {
        IGrid grid = this.getGrid();
        return (grid != null) ? grid.getCache(IEnergyGrid.class) : null;
    }

    /**
     * Gets the essentia monitor from the network.
     *
     * @return The essentia monitor, or null if not available.
     */
//    public IMEEssentiaMonitor getEssentiaMonitor() {
//        IGrid grid = this.getGrid();
//        return (grid != null) ? grid.getCache(IEssentiaGrid.class) : null;
//    }

    /**
     * Gets the item monitor from the network.
     *
     * @return The item monitor, or null if not available.
     */
    public IMEMonitor<IAEItemStack> getItemMonitor() {
        IGrid grid = this.getGrid();
        if (grid != null) {
            IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
            if (storageGrid != null) {
                return storageGrid.getItemInventory();
            }
        }
        return null;
    }

    /**
     * Gets the security grid from the network.
     *
     * @return The security grid, or null if not available.
     */
    public ISecurityGrid getSecurityGrid() {
        IGrid grid = this.getGrid();
        return (grid != null) ? grid.getCache(ISecurityGrid.class) : null;
    }

    @Override
    public double getIdlePowerUsage() {
        // This method should be implemented in the specific part class
        // return this.part.getIdlePowerUsage();
        return 0; // Placeholder
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public boolean isWorldAccessible() {
        return false;
    }

    @Override
    public DimensionalCoord getLocation() {
        return this.part.getLocation();
    }

    @Override
    public AEColor getGridColor() {
        return AEColor.Transparent;
    }

    @Override
    public EnumSet<ForgeDirection> getConnectableSides() {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    @Override
    public IGridHost getMachine() {
        return this.part;
    }

    @Override
    public ItemStack getMachineRepresentation() {
        return this.part.getItemStack(PartItemStack.Network);
    }

    @Override
    public void onGridNotification(GridNotification notification) {
        // Ignored
    }

    @Override
    public void setNetworkStatus(IGrid grid, int usedChannels) {
        // Ignored
    }

    @Override
    public void gridChanged() {
        // Ignored
    }
}
