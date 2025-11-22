package com.ThaumiumAE2.api;

import com.ThaumiumAE2.ThaumiumAE2.implementation.EssentiaStack;
import thaumcraft.api.aspects.Aspect;
import appeng.api.networking.security.BaseActionSource;
import thaumcraft.api.aspects.AspectList;

import java.util.List;


// hate that interface? go tell that to AE2 devs with their shitty thing

public interface IAspectInventory {
    AspectList getStoredEssentia();
    boolean canAccept(Aspect aspect);
	// returns amount of injected
	long injectEssentia(Aspect aspect, long amount, boolean simulate, BaseActionSource src);
	// returns amount of extracted
	long extractEssentia(Aspect aspect, long amount, boolean simulate, BaseActionSource src);

	long getEssentiaAmount(Aspect aspect);
}
