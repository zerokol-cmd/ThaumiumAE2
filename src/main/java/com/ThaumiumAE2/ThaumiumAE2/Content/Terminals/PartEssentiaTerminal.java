package com.ThaumiumAE2.ThaumiumAE2.Content.Terminals;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridNode;
import appeng.api.parts.BusSupport;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import com.ThaumiumAE2.ThaumiumAE2.Implementation.CablePartBase;
import com.ThaumiumAE2.ThaumiumAE2.Implementation.GridBlock;
import com.ThaumiumAE2.ThaumiumAE2.Implementation.TextureManager;
import com.ThaumiumAE2.ThaumiumAE2.Implementation.GUI.CablePartGuiFactory;
import com.ThaumiumAE2.ThaumiumAE2.Interfaces.IGuiHolderProvider;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class PartEssentiaTerminal extends CablePartBase implements ITerminalHost, IGuiHolderProvider<SidedPosGuiData> {

    private static final String NBT_KEY_CONFIG = "config";

    // Icons for rendering
    private IIcon frontIcon;
    private IIcon sideIcon;


    private IIcon screenIconActive;
    private IIcon screenIconInactive;
    private GUIEssentiaTerminal gui;

    public PartEssentiaTerminal(ItemStack associatedItem, SecurityPermissions... requiredPermissions) {
        super(associatedItem, requiredPermissions);
        this.sideIcon = TextureManager.ARCANE_CRAFTING.getTextures()[1];
        this.screenIconActive = screenIconInactive = TextureManager.ARCANE_CRAFTING.getTextures()[1];
        this.frontIcon = TextureManager.ARCANE_CRAFTING.getTextures()[0];
        this.gui = new GUIEssentiaTerminal();
    }

    /**
     *
     */
    @Override
    protected void updateStatus() {
        super.updateStatus();
        this.gui = new GUIEssentiaTerminal(this.getGridBlock().getEssentiaNetwork());
    }

    /**
     *
     */
    @Override
    public void renderInventory(IPartRenderHelper rh, RenderBlocks renderer) {
        // Set the base textures from your texture manager
        rh.setBounds(2, 2, 14, 14, 14, 16);
        rh.renderInventoryBox(renderer);

        // Render the smaller front boxes
        rh.setBounds(4, 4, 13, 12, 12, 14);
        rh.renderInventoryBox(renderer);
        rh.setBounds(5, 5, 12, 11, 11, 13);
        rh.renderInventoryBox(renderer);
    }

    @Override
    public void renderStatic(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer) {
        if (this.frontIcon == null || this.sideIcon == null || this.screenIconActive == null || this.screenIconInactive == null) {
            return; // Don't render if textures are missing
        }

        Tessellator.instance.setColorOpaque_I(0xFFFFFF);
        //main
        rh.setTexture(this.sideIcon, this.sideIcon, this.sideIcon, this.frontIcon, this.sideIcon, this.sideIcon);
        rh.setBounds(2, 2, 13, 14, 14, 15);
        rh.renderBlock(x, y, z, renderer);

        //frame (replaced with 1-pixel border around the main display)
        rh.setTexture(this.sideIcon, this.sideIcon, this.sideIcon, this.sideIcon, this.sideIcon, this.sideIcon);

        // Bottom bar
        rh.setBounds(1, 1, 15, 15, 2, 16);
        rh.renderBlock(x, y, z, renderer);

        // Top bar
        rh.setBounds(1, 14, 15, 15, 15, 16);
        rh.renderBlock(x, y, z, renderer);

        // Left bar
        rh.setBounds(1, 2, 15, 2, 14, 16);
        rh.renderBlock(x, y, z, renderer);

        // Right bar
        rh.setBounds(14, 2, 15, 15, 14, 16);
        rh.renderBlock(x, y, z, renderer);
        //connector
        rh.setBounds(4, 4, 12, 12, 12, 13);
        rh.renderBlock(x, y, z, renderer);
        rh.setBounds(5, 5, 11, 11, 11, 12);
        rh.renderBlock(x, y, z, renderer);


        if (this.isActive()) {
            Tessellator.instance.setBrightness(15728880); // A common brightness value for "on" things
        }
        // Determine which screen icon to use
//        IIcon screenIcon = this.isActive() ? this.screenIconActive : this.screenIconInactive;
//
//        // The screen is on the SOUTH face of the part in this setup.
//        // We render it as a single face on top of the main body.
//        rh.renderFace(x, y, z, screenIcon, ForgeDirection.SOUTH, renderer);
    }

    @Override
    public void renderDynamic(double x, double y, double z, IPartRenderHelper rh, RenderBlocks renderer) {
        // Not needed for a simple terminal
    }


    @Override
    public boolean onActivate(EntityPlayer player, Vec3 pos) {
        if (player.isSneaking()) {
            return false;
        }

        if (!this.getHostTile().getWorldObj().isRemote) {
            int x = this.getHostTile().xCoord;
            int y = this.getHostTile().yCoord;
            int z = this.getHostTile().zCoord;
            CablePartGuiFactory.INSTANCE.open((EntityPlayerMP) player, this.getHostTile(), this.getSide());
        }
        return true;
    }


    @Override
    public IIcon getBreakingTexture() {
        return null;
    }

    @Override
    public int getLightLevel() {
        // Emit light if the terminal is active
        return this.isActive() ? 9 : 0;
    }

    @Override
    public IGridNode getGridNode() {
        //argument doesn't mean anything
        return this.getGridNode(ForgeDirection.DOWN);
    }

    @Override
    public int cableConnectionRenderTo() {
        // Determines the size of the connection to the cable
        return 4;
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random r) {
        // Not needed
    }

    @Override
    public boolean canBePlacedOn(BusSupport type) {
        return type == BusSupport.CABLE;
    }

    @Override
    public void getBoxes(IPartCollisionHelper boxes) {
        // These are the physical collision boxes
        boxes.addBox(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
        boxes.addBox(4.0D, 4.0D, 13.0D, 12.0D, 12.0D, 14.0D);
        boxes.addBox(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 13.0D);
    }


    @Override
    public IMEMonitor<IAEItemStack> getItemInventory() {
        GridBlock gb = this.getGridBlock();
        return (gb != null) ? gb.getItemMonitor() : null;
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        // This terminal does not handle AE2 fluids
        return null;
    }


    @Override
    public IConfigManager getConfigManager() {
        return null;
    }

//    @Override
//    public IMEMonitor<ITAE2EssentiaStack> getEssentiaInventory() {
//        return null;
//    }


    @Override
    public IGuiHolder<SidedPosGuiData> getGuiHolder() {
        return gui;
    }
}
