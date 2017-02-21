package cz.honzamrazek.sensorstreamer;

import cz.honzamrazek.sensorstreamer.models.Packet;

public interface PacketComposer {
    void start(int period);
    void stop();
    void setListener(PacketComposerListener listener);
    interface PacketComposerListener {
        void onPacketComplete(byte[] packet);
    }
}
