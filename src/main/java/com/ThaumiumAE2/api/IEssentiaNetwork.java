package com.ThaumiumAE2.api;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import thaumcraft.api.aspects.Aspect;

import java.util.List;

/**
 * Interface for a container that holds Essentia.
 */
public interface IEssentiaNetwork {
    /**
     * Extracts Essentia from the network.
     *
     * @param aspect The aspect to extract.
     * @param amount The amount to extract.
     * @param simulate If true, the extraction is only simulated.
     * @return The amount of Essentia that was or would be extracted.
     */
    long extractEssentia(Aspect aspect, long amount, boolean simulate);

    /**
     * Injects Essentia into the network.
     *
     * @param aspect The aspect to inject.
     * @param amount The amount to inject.
     * @param simulate If true, the injection is only simulated.
     * @return The amount of Essentia that was or would be injected.
     */
    long injectEssentia(Aspect aspect, long amount, boolean simulate);

    /**
     * Gets the total amount of a specific Essentia in the network.
     *
     * @param aspect The aspect to query.
     * @return The total amount of the specified Essentia.
     */
    long getEssentiaAmount(Aspect aspect);

    /**
     * Gets a list of all Essentia types stored in the network.
     *
     * @return A list of EssentiaStacks representing the stored Essentia.
     */
    List<EssentiaStack> getStoredEssentia();
}
