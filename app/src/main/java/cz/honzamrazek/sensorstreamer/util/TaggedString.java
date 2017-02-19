package cz.honzamrazek.sensorstreamer.util;

public class TaggedString<T> {
    private String string;
    private T tag;

    public TaggedString(String string, T tag) {
        this.string = string;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return string;
    }

    public T getTag() {
        return tag;
    }
}
