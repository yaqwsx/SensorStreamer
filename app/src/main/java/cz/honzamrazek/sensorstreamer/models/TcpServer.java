package cz.honzamrazek.sensorstreamer.models;


public class TcpServer {
    private int port;

    public String getDescription() {
        return "TcpServer connection";
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
