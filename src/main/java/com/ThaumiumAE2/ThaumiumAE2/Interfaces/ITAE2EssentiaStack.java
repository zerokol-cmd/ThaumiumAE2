package com.ThaumiumAE2.ThaumiumAE2.Interfaces;

import appeng.api.storage.data.IAEStack;
import thaumcraft.api.aspects.Aspect;
//рефактори по ка свет есть
// вот бы прямо сейчас выключили ещё полтора часа или час
// что конкретно рефакторить (хз)
// доки
// https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/blob/master/src/main/java/appeng/api/storage/data/IAEStack.java
public interface ITAE2EssentiaStack extends IAEStack<ITAE2EssentiaStack> {

    @Override
    void add(ITAE2EssentiaStack option);

    Aspect getAspect();

}

