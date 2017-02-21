package cz.honzamrazek.sensorstreamer;


import android.content.Context;

import java.io.Closeable;

public abstract class PacketSender implements Closeable {
    public abstract void sendPacket(byte[] packet);

    public interface PacketSenderListener {
        void onError(String error);
    }

    public abstract String getDescription(Context context);
}
