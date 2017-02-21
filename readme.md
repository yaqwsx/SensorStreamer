# SensorStreamer

Our phones are full of usefull sensors - accelerometer, gyroscope,
magnetometer... Every hacker have surely experienced the need to test something
with one of these sensors. Usually you take Arduino with a sensor breakout board
and you wwrite a small piece of code, which send data to serial line.

But not allways you have the sensors you need and the wires are sometimes a
limitation. Here comes SensorStreamer - lightweight Android app, which can log
sensor data and send them over network to your computer, where you can analyze
them e.g. with a simple Python script.

## Supported Features

- streaming of values from any sensor in the phone (as far as the sensor is
  supported by Android API)
- stream data over TCP sockets in
    - client mode
    - server mode
- stream data in
    - JSON object
    - binary packet

## Format of the JSON Packet

The packets contain a JSON object as the top level entity. This object contains a field for each sensor. Each sensor supplies two values:

    - timestamp (in form of nano seconds)
    - single `value` or `x`, `y`, `z` fields with float values. For precise
      meaning of these values, see [Android Sensor Reference](https://developer.
      android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel).

As it is impossible to capture value from multiple sensor at the same time,
timestamp is included for each sensor independently. There should be only small
differences in this timestamps, however, if you application requires precise
timing, this value might come handy.

## Format of the Binary Packet

Binary packet is desgnied to be as simple and as compact as possible. The format
is following:

```
[0x80] [timestamp 8 bytes] [sensor values 1 or 3 float] ... [sensor values 1 or 3 float]
```

Timestamp is optional and can be ommited. The sensors are in the exact same
order as the configuration in app says. This packet contains only one timestamp
in order to be as compact as possible.

## Future Plans

- make the UI more usable
- support UDP connection
- support Bluetooth connection
- bugfixes

## Author

Developed as a quick dirty hack by Jan "yaqwsx" Mrázek