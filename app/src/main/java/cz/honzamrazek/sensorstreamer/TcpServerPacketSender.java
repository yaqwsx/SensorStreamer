package cz.honzamrazek.sensorstreamer;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TcpServerPacketSender extends PacketSender implements Runnable {
    PacketSenderListener mListener;
    BlockingQueue mQueue;
    int mPort;
    ServerSocket mSocketServer;
    Socket mSocket;
    Thread mWorker;

    private class Command {
        public Command(byte[] data) {
            if (data != null) {
                this.stop = false;
                this.data = data;
            }
            else {
                this.stop = true;
            }
        }
        public boolean stop;
        public byte[] data;
    }

    public TcpServerPacketSender(PacketSenderListener listener, int port) {
        mPort = port;
        mListener = listener;
        mQueue = new ArrayBlockingQueue(1024);
        mWorker = new Thread(this);

        mWorker.start();
    }

    @Override
    public void sendPacket(byte[] packet) {
        if (mSocket == null)
            return;
        mQueue.add(new Command(packet));
    }

    @Override
    public String getDescription(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        @SuppressWarnings("deprecation") // formatIP does not support IPv6, neither WifiInfo
        String ipAddress = Formatter.formatIpAddress(wifiInf.getIpAddress());
        return context.getString(R.string.serving_at) + ipAddress + ":" + mPort;
    }

    @Override
    public void close() {
        if (mSocket == null && mSocketServer != null) {
            try {
                mSocketServer.close();
            } catch (IOException e) {/*ignored*/}
        }
        else {
            mQueue.add(new Command(null));
        }
    }

    @Override
    public void run() {
        DataOutputStream stream = null;
        try {
            mSocketServer = new ServerSocket(mPort);
            mSocket = mSocketServer.accept();
            stream = new DataOutputStream(mSocket.getOutputStream());

            while(true) {
                Command c = (Command) mQueue.take();
                if (c.stop)
                    break;
                stream.write(c.data);
            }
        }
        catch (IOException | InterruptedException e) {
            mListener.onError(e.getMessage());
        } finally {
            try {
                if (stream != null)
                    stream.close();
                if (mSocket != null)
                    mSocket.close();
                if (mSocketServer != null)
                    mSocketServer.close();
            }
            catch (IOException exception) {/*ignored*/}
        }
    }
}
