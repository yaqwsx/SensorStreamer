package cz.honzamrazek.sensorstreamer.models;


import android.content.Context;

import cz.honzamrazek.sensorstreamer.R;

public class TcpClient {
    private int port;

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    private String hostname;

    public String getDescription(Context context) {
        return context.getString(R.string.tcp_client_on_host) + hostname + ":" + port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }
}
