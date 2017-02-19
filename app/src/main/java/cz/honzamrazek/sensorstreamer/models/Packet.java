package cz.honzamrazek.sensorstreamer.models;

import android.content.Context;

import cz.honzamrazek.sensorstreamer.R;

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
    public String name;

    public boolean timeStamp;

    public boolean
        accelerometer,
        ambientTemperature,
        gravity,
        gyroscope,
        light,
        linearAcceleration,
        magneticField,
        pressure,
        proximity,
        relativeHumidity,
        rotationVector;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription(Context context) {
        String result = new String();
        if (accelerometer)
            result = addToList(result, context.getString(R.string.accelerometer));
        if (ambientTemperature)
            result = addToList(result, context.getString(R.string.ambient_temperature));
        if (gravity)
            result = addToList(result, context.getString(R.string.gravity));
        if (gyroscope)
            result = addToList(result, context.getString(R.string.gyroscope));
        if (light)
            result = addToList(result, context.getString(R.string.light));
        if (linearAcceleration)
            result = addToList(result, context.getString(R.string.linear_acceleration));
        if (magneticField)
            result = addToList(result, context.getString(R.string.magnetic_field));
        if (pressure)
            result = addToList(result, context.getString(R.string.pressure));
        if (proximity)
            result = addToList(result, context.getString(R.string.proximity));
        if (relativeHumidity)
            result = addToList(result, context.getString(R.string.relative_humidity));
        if (rotationVector)
            result = addToList(result, context.getString(R.string.rotation_vector));
        if (result.isEmpty())
            return context.getString(R.string.empty_packet);
        
        if (type == Type.Binary)
            return context.getString(R.string.binary_packet) + " " + result;
        if (type == Type.JSON)
            return context.getString(R.string.json_packet) + " " + result;
        return context.getString(R.string.invalid_packet);
    }

    private static String addToList(String list, String item) {
        String res = list;
        if (!list.isEmpty())
            res += ", ";
        return res + item;
    }
}
