package com.ThaumiumAE2.ThaumiumAE2.contents.ioBus;

import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.BusSupport;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import com.ThaumiumAE2.ThaumiumAE2.implementation.CablePartBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// filters, upgrades, redstone control, ui (probably?), + on block update, + read/write of everything
// ImportBus and ExportBus just implement importing/exporting stuff using info from filters etc, so make them `protected`
// derivees: ImportBus, ExportBus
// ! NOT StorageBus

public abstract class BasePartIO extends CablePartBase implements IGridTickable, IAEAppEngInventory {
	private static final int MINIMUM_TICKS_PER_OPERATION = 10;
	private static final int MAXIMUM_TICKS_PER_OPERATION = 40;

	private static final int MAX_FILTER_SIZE = 9;


	private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

	protected List<Aspect> filteredAspects = new ArrayList<Aspect>(BasePartIO.MAX_FILTER_SIZE);
	protected int filteredAspectsSize = 0;



    public BasePartIO(ItemStack associatedItem, SecurityPermissions... requiredPermissions) {
        super(associatedItem, requiredPermissions);
    }


	protected boolean isAspectAllowed(Aspect aspect) {
		if (filteredAspectsSize == 0) {
			return true;
		}

		for (int i = 0; i < filteredAspectsSize; i++) {
			if (filteredAspects.get(i) == aspect) {
				return true;
			}
		}

		return false;
	}

	protected abstract boolean perform(int workAmount);

	protected boolean isEnabled() {
		// TODO: redstone
		return true;
	}


    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(MINIMUM_TICKS_PER_OPERATION, MAXIMUM_TICKS_PER_OPERATION, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int TicksSinceLastCall) {
		if (this.isEnabled()) {
			// TODO

            int workAmount = Math.min(TicksSinceLastCall, MAXIMUM_TICKS_PER_OPERATION);
			if (this.perform(workAmount)) {
				return TickRateModulation.URGENT;
			}
		}

        return TickRateModulation.IDLE;
    }

    @Override
    public void renderInventory(IPartRenderHelper rh, RenderBlocks renderer) {

    }

    @Override
    public void renderStatic(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer) {

    }

    @Override
    public void renderDynamic(double x, double y, double z, IPartRenderHelper rh, RenderBlocks renderer) {

    }

    @Override
    public IIcon getBreakingTexture() {
        return null;
    }

    @Override
    public int getLightLevel() {
        return this.isActive() ? 4 : 0;
    }


    @Override
    public int cableConnectionRenderTo() {
        return 4;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random r) {

    }

    @Override
    public boolean canBePlacedOn(BusSupport what) {
        return true;
    }

    @Override
    public void getBoxes(IPartCollisionHelper boxes) {

    }

    @Override
    public void saveChanges() {

    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {

    }
}

