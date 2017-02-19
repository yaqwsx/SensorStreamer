package cz.honzamrazek.sensorstreamer.models;


public class TcpClient {
    private int port;

    public String getDescription() {
        return "TcpClient connection";
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
