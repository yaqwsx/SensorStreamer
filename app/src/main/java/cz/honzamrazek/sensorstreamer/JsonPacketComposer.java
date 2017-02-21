package cz.honzamrazek.sensorstreamer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.gson.Gson;

import cz.honzamrazek.sensorstreamer.models.Packet;

public class JsonPacketComposer implements PacketComposer, SensorEventListener {
    PacketComposerListener mListener;
    SensorManager mManager;
    Packet mPacket;
    int mTargetCount, mCount;
    Json mData;

    private class Vector1D {
        public Vector1D(float[] values, long timestamp) {
            this.value = values[0];
            this.timestamp = timestamp;
        }

        float value;
        long timestamp;
    }

    private class Vector3D {
        public Vector3D(float[] values, long timestamp) {
            this.x = values[0];
            this.y = values[1];
            this.z = values[2];
            this.timestamp = timestamp;
        }

        float x, y, z;
        long timestamp;
    }

    private class Json {
        Vector3D accelerometer;
        Vector1D ambientTemperature;
        Vector3D gravity;
        Vector3D gyroscope;
        Vector1D light;
        Vector3D linearAcceleration;
        Vector3D magneticField;
        Vector3D orientation;
        Vector1D pressure;
        Vector1D proximity;
        Vector1D relativeHumidity;
        Vector3D rotationVector;
    }

    public JsonPacketComposer(SensorManager manager, Packet packet) {
        mManager = manager;
        mPacket = packet;
    }


    @Override
    public void start(int period) {
        mTargetCount = 0;
        if (mPacket.accelerometer) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.ambientTemperature) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.gravity) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.gyroscope) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.light) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.linearAcceleration) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.magneticField) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.pressure) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.proximity) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.relativeHumidity) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.rotationVector) {
            mTargetCount++;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mManager.registerListener(this, s, period);
        }

        mCount = 0;
        mData = new Json();
    }

    @Override
    public void stop() {
        mManager.unregisterListener(this);
    }

    @Override
    public void setListener(PacketComposerListener listener) {
        mListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mData.accelerometer = new Vector3D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                mData.ambientTemperature = new Vector1D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_GRAVITY:
                mData.gravity = new Vector3D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_GYROSCOPE:
                mData.gyroscope = new Vector3D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_LIGHT:
                mData.light = new Vector1D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                mData.linearAcceleration = new Vector3D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mData.magneticField = new Vector3D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_PRESSURE:
                mData.pressure = new Vector1D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_PROXIMITY:
                mData.proximity = new Vector1D(event.values, event. timestamp);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                mData.relativeHumidity = new Vector1D(event.values, event.timestamp);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                mData.rotationVector = new Vector3D(event.values, event.timestamp);
                break;
            default:
                return;
        }
        mCount++;
        if (mCount == mTargetCount) {
            mCount = 0;
            String packet = new Gson().toJson(mData);
            packet += "\n";
            mListener.onPacketComplete(packet.getBytes());
            mData = new Json();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
