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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.models.Connection;

public class TcpClientConnectionFragment extends Fragment implements SpecialisedConnectionInterface {
    private OnFragmentInteractionListener mListener;
    private EditText mHostname;
    private EditText mPort;

    public TcpClientConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tcp_client_connection, container, false);
        mPort = (EditText) view.findViewById(R.id.port);
        mHostname = (EditText) view.findViewById(R.id.hostname);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mListener.onValidation(isValid());
            }
        };

        mPort.addTextChangedListener(watcher);
        mHostname.addTextChangedListener(watcher);

        Connection c = mListener.onDataLoad();
        if (c.tcpClient.getPort() > 0)
            mPort.setText(Integer.toString(c.tcpClient.getPort()));
        mHostname.setText(c.tcpClient.getHostname());

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
        boolean port = validatePort();
        boolean hostname = validateHostname();
        return port && hostname;
    }

    @Override
    public void commit(Connection connection) {
        int port = Integer.parseInt(mPort.getText().toString());
        connection.tcpClient.setPort(port);
        connection.tcpClient.setHostname(mHostname.getText().toString());
    }

    private boolean validateHostname() {
        if (mHostname == null)
            return false;
        String content = mHostname.getText().toString();
        if (content.isEmpty()) {
            mHostname.setError(getString(R.string.hostname_has_to_be_specified));
            return false;
        }
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(content);
        boolean foundWhitespace = matcher.find();
        if (foundWhitespace) {
            mHostname.setError(getString(R.string.hostname_cannot_contain_spaces));
            return false;
        }
        return true;
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
