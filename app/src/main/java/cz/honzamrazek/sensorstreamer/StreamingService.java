package cz.honzamrazek.sensorstreamer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Spinner;

import java.io.IOException;

import cz.honzamrazek.sensorstreamer.models.Connection;
import cz.honzamrazek.sensorstreamer.models.Packet;

public class StreamingService extends Service
        implements PacketComposer.PacketComposerListener, PacketSender.PacketSenderListener {
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String CONNECTION = "connection";
    public static final String PACKET = "packet";
    public static final String PERIOD = "period";

    private PacketComposer mComposer;
    private PacketSender mSender;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(START)) {
            int period = intent.getIntExtra(PERIOD, 0);
            SharedStorageManager<Packet> packetManager = new SharedStorageManager<>(this, Packet.class);
            int packetId = intent.getIntExtra(PACKET, Spinner.INVALID_POSITION);
            if (packetId == Spinner.INVALID_POSITION) {
                dieWithError(getString(R.string.invalid_packet), getString(R.string.no_packet_specified));
                return START_NOT_STICKY;
            }
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Packet packet = packetManager.get(packetId);
            switch (packet.type) {
                case JSON:
                    mComposer = new JsonPacketComposer(sensorManager, packet);
                    break;
                case Binary:
                    mComposer = new BinaryPacketComposer(sensorManager, packet);
                    break;
            }
            mComposer.setListener(this);
            mComposer.start(period);

            SharedStorageManager<Connection> connectionManager = new SharedStorageManager<>(this, Connection.class);
            int connectionId = intent.getIntExtra(CONNECTION, Spinner.INVALID_POSITION);
            if (connectionId == Spinner.INVALID_POSITION) {
                dieWithError(getString(R.string.invalid_connection), getString(R.string.no_connection_specified));
                return START_NOT_STICKY;
            }
            Connection connection = connectionManager.get(connectionId);

            switch (connection.type) {
                case TcpClient:
                    mSender = new TcpClientPacketSender(this,
                            connection.tcpClient.getHostname(), connection.tcpClient.getPort());
                    break;
                case TcpServer:
                    mSender = new TcpServerPacketSender(this, connection.tcpServer.getPort());
                    break;
                default:
                    dieWithError("Unsupported connection", "Selected type of connection is not supported");
                    return START_NOT_STICKY;
            }

            Intent stop = new Intent(this, StreamingService.class);
            stop.setAction(STOP);
            PendingIntent pStop = PendingIntent.getService(this, 0, stop, 0);

            Notification.Builder builder = new Notification.Builder(getBaseContext())
                    //.setTicker("Your Ticker")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(getString(R.string.streaming_is_active))
                    .setContentText(mSender.getDescription(this))
                    .addAction(R.drawable.ic_stat_name, getString(R.string.stop), pStop);

            startForeground(1, builder.build());
        }
        else if(intent.getAction().equals(STOP)) {
            try {
                if (mComposer != null)
                    mComposer.stop();
                if (mSender != null)
                    mSender.close();
            } catch (IOException e) {
                onError(e.getMessage());
            }
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

    public void dieWithError(String title, String message) {
        errorMessage(title, message);
        stopSelf();
        if (mComposer != null)
            mComposer.stop();
        if (mSender != null) {
            try {
                mSender.close();
            } catch (IOException e) { /* ignored */ }
        }
    }

    @Override
    public void onPacketComplete(byte[] packet) {
        mSender.sendPacket(packet);
    }

    @Override
    public void onError(String error) {
        dieWithError(getString(R.string.communication_error), error);
    }
}
