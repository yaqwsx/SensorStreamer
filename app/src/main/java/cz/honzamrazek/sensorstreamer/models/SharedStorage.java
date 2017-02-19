package cz.honzamrazek.sensorstreamer.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface SharedStorage {
    String storageName();
    int storageVersion();
    String keyName();
}
