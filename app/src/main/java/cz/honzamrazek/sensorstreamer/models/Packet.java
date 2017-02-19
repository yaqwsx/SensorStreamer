package cz.honzamrazek.sensorstreamer.models;

@SharedStorage(storageName = "Data", storageVersion = 1, keyName = "Packets")
public class Packet implements Descriptionable {
    public enum Type {Empty, JSON, Binary}

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type type;
    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return "Just a packet";
    }
}
