package cz.honzamrazek.sensorstreamer.models;

import android.content.Context;

@SharedStorage(storageName = "Data", storageVersion = 1, keyName = "Connections")
public class Connection implements Descriptionable {
    public enum Type {Empty, TcpClient, TcpServer}

    public Type type;
    public String name;

    // Variant would be useful, but to make things simple for DBFlow...
    public TcpClient tcpClient;
    public TcpServer tcpServer;

    public Connection() {
        this.type = Type.Empty;
    }

    public Connection(String name, Type type) {
        this.name = name;
        setType(type);
    }

    public String getName() {
        return name;
    }

    public Object getData() {
        switch (type) {
            case TcpClient:
                return tcpClient;
            case TcpServer:
                return tcpServer;
            default:
                throw new IllegalStateException("Inconsistent Connection");
        }
    }

    public String getDescription(Context context) {
        switch(type) {
            case TcpClient:
                return tcpClient.getDescription(context);
            case TcpServer:
                return tcpServer.getDescription(context);
            default:
                throw new IllegalStateException("Inconsistent Connection");
        }
    }

    public void setType(Type type) {
        this.type = type;

        switch (type) {
            case TcpClient:
                if (tcpClient == null)
                    tcpClient = new TcpClient();
                break;
            case TcpServer:
                if (tcpServer == null)
                    tcpServer = new TcpServer();
                break;
        }
    }

    public void sanitize() {
        if (type != Type.TcpClient)
            tcpClient = null;
        if (type != Type.TcpServer)
            tcpServer = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTcpClient(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    public void setTcpServer(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    public Type getType() {
        return type;
    }
}
