package cz.honzamrazek.sensorstreamer;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import cz.honzamrazek.sensorstreamer.activities.MainActivity;
import cz.honzamrazek.sensorstreamer.models.Packet;
import cz.honzamrazek.sensorstreamer.models.SharedStorage;

public class StreamingService extends Service implements PacketComposer.PacketComposerListener {
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String CONNECTION = "connection";
    public static final String PACKET = "packet";
    public static final String FREQ = "frequency";

    private PacketComposer mComposer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(START)) {
            Intent stop = new Intent(this, StreamingService.class);
            stop.setAction(STOP);
            PendingIntent pStop = PendingIntent.getService(this, 0, stop, 0);

            Notification.Builder builder = new Notification.Builder(getBaseContext())
                    .setTicker("Your Ticker")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentTitle("Your content title")
                    .setContentText("Your content text")
                    .addAction(android.R.drawable.ic_media_pause, "Stop", pStop);

            startForeground(1, builder.build());

            String freqStr = intent.getStringExtra(FREQ);
            if (freqStr.isEmpty()) {
                errorMessage("Invalid frequency", "Invalid frequency was specified");
                stopSelf();
                return START_NOT_STICKY;
            }
            int frequency = Integer.parseInt(freqStr);

            SharedStorageManager<Packet> packetManager = new SharedStorageManager<>(this, Packet.class);
            int packetId = intent.getIntExtra(PACKET, Spinner.INVALID_POSITION);
            if (packetId == Spinner.INVALID_POSITION) {
                errorMessage(getString(R.string.invalid_packet), getString(R.string.no_packet_specified));
                stopSelf();
                return START_NOT_STICKY;
            }
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Packet packet = packetManager.get(packetId);
            switch (packet.type) {
                case JSON:
                    mComposer = new JsonPacketComposer(sensorManager, packet);
                    break;
                case Binary:
                    errorMessage(getString(R.string.not_supported), getString(R.string.binary_not_supported));
                    stopSelf();
                    return START_NOT_STICKY;
            }
            mComposer.setListener(this);
            mComposer.start(frequency);
        }
        else if(intent.getAction().equals(STOP)) {
            mComposer.stop();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void errorMessage(String title, String message) {
        Intent dialog = new Intent(this, StreamingErrorActivity.class);
        dialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialog.putExtra("title", title);
        dialog.putExtra("message", message);
        startActivity(dialog);
    }

    @Override
    public void onPacketComplete(byte[] packet) {
        String str = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            str = new String(packet, StandardCharsets.UTF_8);
        }
        Log.d("Packets", str);
    }
}
