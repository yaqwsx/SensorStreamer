package cz.honzamrazek.sensorstreamer;


public interface PacketSender {
    void sendPacket(Byte[] packet);
}
