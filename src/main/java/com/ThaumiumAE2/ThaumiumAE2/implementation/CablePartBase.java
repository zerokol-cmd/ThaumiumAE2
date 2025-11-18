package com.ThaumiumAE2.ThaumiumAE2.implementation;

import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A base class for ThaumiumAE2 cable parts, inspired by ThEPartBase.
 */
public abstract class CablePartBase implements IPart, IGridHost, IActionHost {

    private static final String NBT_KEY_OWNER = "Owner";

    private IPartHost host;
    private TileEntity hostTile;
    private ForgeDirection side;
    private IGridNode node;
    private int ownerID = -1;
    private boolean isActive;
    private boolean isPowered;

    private final ItemStack associatedItem;
    private final SecurityPermissions[] requiredPermissions;
    protected GridBlock gridBlock;

    public CablePartBase(ItemStack associatedItem, SecurityPermissions... requiredPermissions) {
        this.associatedItem = associatedItem;
        this.requiredPermissions = requiredPermissions;
    }

    protected GridBlock getGridBlock() {
        // Lazily initialize the grid block
        if (this.gridBlock == null) {
            this.gridBlock = new GridBlock(this);
        }
        return this.gridBlock;
    }

    private void updateStatus() {
        if (this.hostTile == null || this.hostTile.getWorldObj() == null || this.hostTile.getWorldObj().isRemote) {
            return;
        }

        if (this.node != null) {
            boolean currentlyActive = this.node.isActive();
            if (currentlyActive != this.isActive) {
                this.isActive = currentlyActive;
                this.host.markForUpdate();
            }
        }
        this.onNeighborChanged();
    }

    protected boolean hasPermission(EntityPlayer player, SecurityPermissions permission) {
        if (this.getGridBlock() != null && this.getGridBlock().getSecurityGrid() != null) {
            return this.getGridBlock().getSecurityGrid().hasPermission(player, permission);
        }
        return false;
    }

    // protected TileEntity getFacingTile() {
    //     if (this.hostTile == null) {
    //         return null;
    //     }
    //     World world = this.hostTile.getWorldObj();
    //     if (world == null) {
    //         return null;
    //     }
    //     return world.getTileEntity(
    //         this.hostTile.xCoord + this.side.offsetX,
    //         this.hostTile.yCoord + this.side.offsetY,
    //         this.hostTile.zCoord + this.side.offsetZ);
    // }

    @Override
    public void addToWorld() {
        if (this.hostTile != null && !this.hostTile.getWorldObj().isRemote) {
            this.node = AEApi.instance().createGridNode(this.getGridBlock());
            if (this.node != null) {
                this.node.setPlayerID(this.ownerID);
                this.node.updateState();
            }
            this.updateStatus();
        }
    }

    @Override
    public void removeFromWorld() {
        if (this.node != null) {
            this.node.destroy();
        }
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        if(node == null){
            this.node = AEApi.instance().createGridNode(this.getGridBlock());
        }
        return this.node;
    }

    public IGridNode getExternalFacingNode() {
        return null;
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak() {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(this.getItemStack(PartItemStack.Break));
        this.getDrops(drops, false);

        if (this.hostTile != null && this.hostTile.getWorldObj() != null) {
            appeng.util.Platform.spawnDrops(
                this.hostTile.getWorldObj(),
                this.hostTile.xCoord,
                this.hostTile.yCoord,
                this.hostTile.zCoord,
                drops);
        }
        this.host.removePart(this.side, false);
    }

    @Override
    public void setPartHostInfo(ForgeDirection side, IPartHost host, TileEntity tile) {
        this.side = side;
        this.host = host;
        this.hostTile = tile;
    }

    @Override
    public final void writeToNBT(NBTTagCompound data) {
        this.writeToNBT(data, PartItemStack.World);
    }

    public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
        if (saveType == PartItemStack.World) {
            data.setInteger(NBT_KEY_OWNER, this.ownerID);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        if (data.hasKey(NBT_KEY_OWNER)) {
            this.ownerID = data.getInteger(NBT_KEY_OWNER);
        }
    }

    @Override
    public void writeToStream(ByteBuf data) throws IOException {
        data.writeBoolean(this.isActive());
        data.writeBoolean(this.isPowered());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean readFromStream(ByteBuf data) throws IOException {
        final boolean oldActive = this.isActive;
        final boolean oldPowered = this.isPowered;

        this.isActive = data.readBoolean();
        this.isPowered = data.readBoolean();

        return oldActive != this.isActive || oldPowered != this.isPowered;
    }

    @Override
    public IGridNode getActionableNode() {
        return this.node;
    }

    @Override
    public ItemStack getItemStack(PartItemStack type) {
        ItemStack stack = this.associatedItem.copy();
        if (type == PartItemStack.Wrench) {
            NBTTagCompound data = new NBTTagCompound();
            this.writeToNBT(data, type);
            if (!data.hasNoTags()) {
                stack.setTagCompound(data);
            }
        }
        return stack;
    }

    @Override
    public boolean onActivate(EntityPlayer player, Vec3 pos) {
        return false;
    }

    @Override
    public boolean onShiftActivate(EntityPlayer player, Vec3 pos) {
        return false;
    }

    @Override
    public void onPlacement(EntityPlayer player, ItemStack held, ForgeDirection side) {
        this.ownerID = AEApi.instance().registries().players().getID(player.getGameProfile());
    }

    @Override
    public void getDrops(List<ItemStack> drops, boolean wrenched) {}

    @Override
    public boolean requireDynamicRender() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canConnectRedstone() {
        return false;
    }

    @Override
    public void onNeighborChanged() {}

    @Override
    public int isProvidingStrongPower() {
        return 0;
    }

    @Override
    public int isProvidingWeakPower() {
        return 0;
    }

    @Override
    public void onEntityCollision(Entity entity) {}

    @Override
    public boolean isLadder(EntityLivingBase entity) {
        return false;
    }

    public IPartHost getHost() {
        return this.host;
    }

    public TileEntity getHostTile() {
        return this.hostTile;
    }

    public ForgeDirection getSide() {
        return this.side;
    }

    public DimensionalCoord getLocation() {
        if (this.hostTile == null) {
            return null;
        }
        return new DimensionalCoord(this.hostTile);
    }

    public boolean isActive() {
        if (this.hostTile != null && this.hostTile.getWorldObj() != null && this.hostTile.getWorldObj().isRemote) {
            return this.isActive;
        }
        if (this.node != null) {
            return this.node.isActive();
        }
        return false;
    }

    public boolean isPowered() {
        if (this.hostTile != null && this.hostTile.getWorldObj() != null && this.hostTile.getWorldObj().isRemote) {
            return this.isPowered;
        }
        try {
            if (this.getGridBlock() != null && this.getGridBlock().getEnergyGrid() != null) {
                return this.getGridBlock().getEnergyGrid().isNetworkPowered();
            }
        } catch (Exception e) {
            // Ignored
        }
        return false;
    }

    protected void markForUpdate() {
        if (this.host != null) {
            this.host.markForUpdate();
        }
    }

    protected void markForSave() {
        if (this.host != null) {
            this.host.markForSave();
        }
    }

    @MENetworkEventSubscribe
    public final void onPowerUpdate(MENetworkPowerStatusChange event) {
        this.updateStatus();
    }

    @MENetworkEventSubscribe
    public final void onChannelUpdate(MENetworkChannelsChanged event) {
        this.updateStatus();
    }

}
