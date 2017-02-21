package cz.honzamrazek.sensorstreamer.models;


import android.content.Context;

import cz.honzamrazek.sensorstreamer.R;

public class TcpServer {
    private int port;

    public String getDescription(Context context) {
        return context.getString(R.string.tcp_server_on_port) + port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
