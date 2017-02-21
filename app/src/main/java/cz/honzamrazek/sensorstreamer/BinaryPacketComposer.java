package cz.honzamrazek.sensorstreamer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.nio.ByteBuffer;

import cz.honzamrazek.sensorstreamer.models.Packet;

public class BinaryPacketComposer implements PacketComposer, SensorEventListener {
    private PacketComposerListener mListener;
    private SensorManager mManager;
    private Packet mPacket;
    private int mTargetCount, mCount;
    private ByteBuffer mData;

    private int accelerometerOffset,
        ambientTemperatureOffset,
        gravityOffset,
        gyroscopeOffset,
        lightOffset,
        linearAccelerationOffset,
        magneticFieldOffset,
        pressureOffset,
        proximityOffset,
        relativeHumidityOffset,
        rotationVectorOffset;

    public BinaryPacketComposer(SensorManager manager, Packet packet) {
        mManager = manager;
        mPacket = packet;
    }


    @Override
    public void start(int period) {
        mTargetCount = 0;
        int size = 1;
        if (mPacket.timeStamp)
            size += 8;

        if (mPacket.accelerometer) {
            mTargetCount++;
            accelerometerOffset = size;
            size += 12;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.ambientTemperature) {
            mTargetCount++;
            ambientTemperatureOffset = size;
            size += 4;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.gravity) {
            mTargetCount++;
            gravityOffset = size;
            size += 12;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.gyroscope) {
            mTargetCount++;
            gyroscopeOffset = size;
            size += 12;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.light) {
            mTargetCount++;
            lightOffset = size;
            size += 4;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.linearAcceleration) {
            mTargetCount++;
            linearAccelerationOffset = size;
            size += 12;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.magneticField) {
            mTargetCount++;
            magneticFieldOffset = size;
            size += 12;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.pressure) {
            mTargetCount++;
            pressureOffset = size;
            size += 4;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.proximity) {
            mTargetCount++;
            proximityOffset = size;
            size += 4;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.relativeHumidity) {
            mTargetCount++;
            relativeHumidityOffset = size;
            size += 4;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            mManager.registerListener(this, s, period);
        }

        if (mPacket.rotationVector) {
            mTargetCount++;
            rotationVectorOffset = size;
            size += 12;
            Sensor s = mManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mManager.registerListener(this, s, period);
        }

        mCount = 0;
        mData = ByteBuffer.allocateDirect(size);
        mData.put((byte) 0x80);
    }

    @Override
    public void stop() {
        mManager.unregisterListener(this);
    }

    @Override
    public void setListener(PacketComposerListener listener) {
        mListener = listener;
    }

    private void putN(int offset, int count, float[] data) {
        for (int i = 0; i != count; i++)
            mData.putFloat(offset + 4 * i, data[i]);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                putN(accelerometerOffset, 3, event.values);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                putN(ambientTemperatureOffset, 1, event.values);
                break;
            case Sensor.TYPE_GRAVITY:
                putN(gravityOffset, 3, event.values);
                break;
            case Sensor.TYPE_GYROSCOPE:
                putN(gyroscopeOffset, 3, event.values);
                break;
            case Sensor.TYPE_LIGHT:
                putN(lightOffset, 1, event.values);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                putN(linearAccelerationOffset, 3, event.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                putN(magneticFieldOffset, 3, event.values);
                break;
            case Sensor.TYPE_PRESSURE:
                putN(pressureOffset, 1, event.values);
                break;
            case Sensor.TYPE_PROXIMITY:
                putN(proximityOffset, 1, event.values);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                putN(relativeHumidityOffset, 1, event.values);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                putN(rotationVectorOffset, 3, event.values);
                break;
            default:
                return;
        }
        mCount++;
        if (mCount == mTargetCount) {
            if (mPacket.timeStamp)
                mData.putLong(1, event.timestamp);
            mCount = 0;
            mListener.onPacketComplete(mData.array());
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
