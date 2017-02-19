package cz.honzamrazek.sensorstreamer.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.SharedStorageManager;
import cz.honzamrazek.sensorstreamer.adapters.ItemOverviewAdapter;
import cz.honzamrazek.sensorstreamer.models.Connection;
import cz.honzamrazek.sensorstreamer.models.Packet;
import cz.honzamrazek.sensorstreamer.models.SharedStorage;


public class StreamFragment extends Fragment {
    private SharedStorageManager<Connection> mConnectionsManager;
    private ItemOverviewAdapter<Connection> mConnectionsAdapter;
    private SharedStorageManager<Packet> mPacketsManager;
    private ItemOverviewAdapter<Packet> mPacketsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stream, container, false);

        mConnectionsManager = new SharedStorageManager<>(getActivity(), Connection.class);
        mConnectionsAdapter = new ItemOverviewAdapter<>(getActivity(), mConnectionsManager.getItems());
        Spinner connection = (Spinner) v.findViewById(R.id.connection);
        connection.setAdapter(mConnectionsAdapter);

        mPacketsManager = new SharedStorageManager<>(getActivity(), Packet.class);
        mPacketsAdapter = new ItemOverviewAdapter<>(getActivity(), mPacketsManager.getItems());
        Spinner packet = (Spinner) v.findViewById(R.id.packet);
        packet.setAdapter(mPacketsAdapter);

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
}
