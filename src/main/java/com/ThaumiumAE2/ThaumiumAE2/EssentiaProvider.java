package com.ThaumiumAE2.ThaumiumAE2;

import appeng.api.storage.StorageChannel;
import com.ThaumiumAE2.api.ITAE2EssentiaStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EssentiaProvider {
    static public void provide() {
        try {
            Constructor<StorageChannel> constructor = StorageChannel.class.getDeclaredConstructor(
                String.class, int.class, Class.class
            );
            constructor.setAccessible(true);

            int nextOrdinal = StorageChannel.values().length;
            TAE2.ESSENTIA_STORAGE = constructor.newInstance(
                "ESSENTIA",
                nextOrdinal,
                ITAE2EssentiaStack.class
            );
        }
        catch(Exception e)
        {
            System.out.println("Got exception while binding to storagechannel"  + e.toString());
        }
    }
}
