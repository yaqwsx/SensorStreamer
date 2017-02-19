package cz.honzamrazek.sensorstreamer.models;

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

    public String getDescription() {
        switch(type) {
            case TcpClient:
                return tcpClient.getDescription();
            case TcpServer:
                return tcpServer.getDescription();
            default:
                throw new IllegalStateException("Inconsistent Connection");
        }
    }

    public void setType(Type type) {
        this.type = type;
        tcpClient = null;
        tcpServer = null;

        switch (type) {
            case TcpClient:
                tcpClient = new TcpClient();
                break;
            case TcpServer:
                tcpServer = new TcpServer();
                break;
        }
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
