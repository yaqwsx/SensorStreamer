package cz.honzamrazek.sensorstreamer;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TcpClientPacketSender extends PacketSender implements Runnable {
    PacketSenderListener mListener;
    BlockingQueue mQueue;
    String mHostname;
    int mPort;
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

    public TcpClientPacketSender(PacketSenderListener listener, String hostname, int port) {
        mHostname = hostname;
        mPort = port;
        mListener = listener;
        mQueue = new ArrayBlockingQueue(1024);
        mWorker = new Thread(this);

        mWorker.start();
    }

    @Override
    public void sendPacket(byte[] packet) {
        mQueue.add(new Command(packet));
    }

    @Override
    public String getDescription(Context context) {
        return context.getString(R.string.connected_to) + mHostname + ":" + mPort;
    }

    @Override
    public void close() {
        mQueue.add(new Command(null));
    }

    @Override
    public void run() {
        Socket socket = null;
        DataOutputStream stream = null;
        try {
            socket = new Socket(mHostname, mPort);
            stream = new DataOutputStream(socket.getOutputStream());

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
                if (socket != null)
                    socket.close();
            }
            catch (IOException exception) {/*ignored*/}
        }
    }
}
