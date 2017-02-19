package cz.honzamrazek.sensorstreamer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.fragments.SpecialisedConnectionInterface;
import cz.honzamrazek.sensorstreamer.models.Connection;

public class TcpServerConnectionFragment extends Fragment implements SpecialisedConnectionInterface {
    private OnFragmentInteractionListener mListener;
    private EditText mPort;

    public TcpServerConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tcp_server_connection, container, false);
        mPort = (EditText) view.findViewById(R.id.port);
        mPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mListener.onValidation(isValid());
            }
        });
        Connection c = mListener.onDataLoad();
        if (c.tcpServer.getPort() > 0)
            mPort.setText(Integer.toString(c.tcpServer.getPort()));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean isValid() {
        return validatePort();
    }

    @Override
    public void commit(Connection connection) {
        int port = Integer.parseInt(mPort.getText().toString());
        connection.tcpServer.setPort(port);
    }

    private boolean validatePort() {
        if (mPort == null)
            return false;
        String content = mPort.getText().toString();
        if (content.isEmpty()) {
            mPort.setError(getString(R.string.port_has_to_be_specified));
            return false;
        }
        int port = Integer.parseInt(content);
        if (port < 1 || port > 65536) {
            mPort.setError(getString(R.string.port_outside_range));
            return false;
        }
        mPort.setError(null);
        return true;
    }
}
