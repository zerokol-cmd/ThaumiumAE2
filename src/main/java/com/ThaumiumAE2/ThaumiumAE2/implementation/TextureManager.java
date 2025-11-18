package com.ThaumiumAE2.ThaumiumAE2.implementation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.TextureStitchEvent;
import com.ThaumiumAE2.ThaumiumAE2.TAE2;

/**
 * Global texture manager for all ThaumiumAE2 textures.
 * <p>
 * This class provides easy setup for textures across terminals, blocks, parts, etc.
 * Each enum constant represents a texture set (e.g., for a specific part or block).
 * It supports multiple icons per set, automatic prefix handling (e.g., "parts/"),
 * and AE2 part model registration.
 * <p>
 * Usage:
 * - Add enum constants for new texture sets.
 * - Call TextureManager.registerAll() in your ClientProxy via TextureStitchEvent.Pre.
 * - Access icons via getters like getIcon(0) or custom methods (e.g., getFront()).
 * <p>
 * Example access: TextureManager.ARCANE_CRAFTING.getFront()
 */

@SideOnly(Side.CLIENT)
public enum TextureManager {
    // ====================================================================
    // Inner types
    // ====================================================================
    // Example for a terminal/part (adjust names and counts as needed)
    ARCANE_CRAFTING(TextureTypes.Part,
        "essentia_terminal_front",
        "essentia_terminal_side"
    );

    enum TextureTypes {
        Block, // No special prefix, just modid:
        Part // Prefix with "parts/" and register with AE2 PartModels
    }
    /**
     * Cached list of all values – useful for iteration
     */
    public static final List<TextureManager> VALUES =
        Collections.unmodifiableList(Arrays.asList(values()));
    // ====================================================================
    // Fields
    // ====================================================================
    private final TextureTypes type;
    private final String[] textureNames;
    private final IIcon[] icons;

    // ====================================================================
    // Constructor
    // ====================================================================
    private TextureManager(final TextureTypes type, final String... textureNames) {
        this.type = type;
        this.textureNames = textureNames;
        this.icons = new IIcon[textureNames.length];
    }
    // ====================================================================
    // Public Accessors
    // ====================================================================

    /**
     * @return The first IIcon (e.g., primary/main texture).
     */
    public IIcon getTexture() {
        return (icons.length > 0) ? icons[0] : null;
    }

    /**
     * @return All IIcons for this texture set.
     */
    public IIcon[] getTextures() {
        return icons.clone(); // Defensive copy
    }

    /**
     * Generic getter for a specific icon by index.
     *
     * @param index The index of the texture (0-based).
     * @return The IIcon at the given index, or null if out of bounds.
     */
    public IIcon getIcon(int index) {
        return (index >= 0 && index < icons.length) ? icons[index] : null;
    }

    // Custom convenience methods (add more as needed for specific sets)
    // These assume a common order; adjust per enum constant if needed
    public IIcon getFront() {
        return getIcon(0);
    }

    public IIcon getSide() {
        return getIcon(1);
    }

    public IIcon overlay1() {
        return getIcon(0);
    }

    public IIcon overlay2() {
        return getIcon(1);
    }
    // ====================================================================
    // Texture Registration
    // ====================================================================

    /**
     * Registers the icons for this specific texture set.
     * Called internally by registerAll().
     *
     * @param map The TextureMap to register with.
     */
    private void register(final TextureMap map) {
        final String prefix = TAE2.MODID + (type == TextureTypes.Part ? ":parts/" : ":");
        for (int i = 0; i < textureNames.length; i++) {
            icons[i] = map.registerIcon(prefix + textureNames[i]);
        }
    }

    /**
     * Static helper to register ALL textures for every set defined in this enum.
     * Call this once from your ClientProxy during the TextureStitchEvent.Pre event.
     *
     * @param map The TextureMap used for registering blocks and items.
     */
    public static void registerAll(final TextureMap map) {
        // Only register on the main texture sheet (type 0: blocks/items/parts)
        if (map.getTextureType() != 0) {
            return;
        }
        for (final TextureManager textureSet : VALUES) {
            textureSet.register(map);
        }
    }
    // ====================================================================
    // Forge Event Integration (Global Handler)
    // ====================================================================

    /**
     * Global event handler for automatic texture registration.
     * Register this class (or an instance) with MinecraftForge.EVENT_BUS in your ClientProxy.
     */
    @SideOnly(Side.CLIENT)
    public static class TextureEventHandler {
        @SubscribeEvent
        public void onTextureStitch(TextureStitchEvent.Pre event) {
            TextureManager.registerAll(event.map);
        }
    }
}

