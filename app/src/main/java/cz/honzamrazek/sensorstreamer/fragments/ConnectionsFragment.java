package cz.honzamrazek.sensorstreamer.fragments;

import android.content.Intent;

import cz.honzamrazek.sensorstreamer.activities.EditConnectionActivity;
import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.models.Connection;

public class ConnectionsFragment extends ItemsOverviewFragment<Connection> {

    public ConnectionsFragment() {
        super(Connection.class, R.string.no_connections);
    }

    @Override
    public void editItem(int position) {
        Intent intent = new Intent(getActivity(), EditConnectionActivity.class);
        intent.putExtra(EditConnectionActivity.EXTRA_CONNECTION, position);
        startActivity(intent);
    }

    @Override
    public void createNewItem() {
        Intent intent = new Intent(getActivity(), EditConnectionActivity.class);
        startActivity(intent);
    }
}
