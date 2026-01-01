package com.ThaumiumAE2.ThaumiumAE2.Interfaces;

import appeng.api.networking.security.BaseActionSource;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;


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
