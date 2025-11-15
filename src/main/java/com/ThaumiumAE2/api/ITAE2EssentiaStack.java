package com.ThaumiumAE2.api;

import appeng.api.storage.data.IAEStack;

// доки https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/blob/master/src/main/java/appeng/api/storage/data/IAEStack.java
public interface ITAE2EssentiaStack extends IAEStack<ITAE2EssentiaStack> {
    @Override
    void add(ITAE2EssentiaStack option);
}
