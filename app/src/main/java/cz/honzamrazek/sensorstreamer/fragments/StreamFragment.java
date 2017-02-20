package cz.honzamrazek.sensorstreamer.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.SharedStorageManager;
import cz.honzamrazek.sensorstreamer.StreamingService;
import cz.honzamrazek.sensorstreamer.adapters.ItemOverviewAdapter;
import cz.honzamrazek.sensorstreamer.models.Connection;
import cz.honzamrazek.sensorstreamer.models.Packet;
import cz.honzamrazek.sensorstreamer.models.SharedStorage;


public class StreamFragment extends Fragment {
    private SharedStorageManager<Connection> mConnectionsManager;
    private ItemOverviewAdapter<Connection> mConnectionsAdapter;
    private Spinner mConnectionsSpinner;
    private SharedStorageManager<Packet> mPacketsManager;
    private ItemOverviewAdapter<Packet> mPacketsAdapter;
    private Spinner mPacketsSpinner;
    private EditText mFrequency;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stream, container, false);

        mConnectionsManager = new SharedStorageManager<>(getActivity(), Connection.class);
        mConnectionsAdapter = new ItemOverviewAdapter<>(getActivity(), mConnectionsManager.getItems());
        mConnectionsSpinner = (Spinner) v.findViewById(R.id.connection);
        mConnectionsSpinner.setAdapter(mConnectionsAdapter);

        mPacketsManager = new SharedStorageManager<>(getActivity(), Packet.class);
        mPacketsAdapter = new ItemOverviewAdapter<>(getActivity(), mPacketsManager.getItems());
        mPacketsSpinner = (Spinner) v.findViewById(R.id.packet);
        mPacketsSpinner.setAdapter(mPacketsAdapter);

        Button startButton = (Button) v.findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStreaming();
            }
        });

        Button stopButton = (Button) v.findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStreaming();
            }
        });

        mFrequency = (EditText) v.findViewById(R.id.frequency);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mConnectionsManager.reload();
        mConnectionsAdapter.notifyDataSetChanged();
        mPacketsManager.reload();
        mPacketsAdapter.notifyDataSetChanged();
    }

    public void startStreaming() {
        Intent intent = new Intent(getActivity(), StreamingService.class);
        intent.setAction(StreamingService.START);
        intent.putExtra(StreamingService.CONNECTION, mConnectionsSpinner.getSelectedItemPosition());
        intent.putExtra(StreamingService.PACKET, mPacketsSpinner.getSelectedItemPosition());
        intent.putExtra(StreamingService.FREQ, mFrequency.getText().toString());
        getActivity().startService(intent);
    }

    public void stopStreaming() {
        Intent intent = new Intent(getActivity(), StreamingService.class);
        intent.setAction(StreamingService.STOP);
        getActivity().startService(intent);
    }
}
