package cz.honzamrazek.sensorstreamer.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.SharedStorageManager;
import cz.honzamrazek.sensorstreamer.fragments.TcpClientConnectionFragment;
import cz.honzamrazek.sensorstreamer.fragments.TcpServerConnectionFragment;
import cz.honzamrazek.sensorstreamer.fragments.SpecialisedConnectionInterface;
import cz.honzamrazek.sensorstreamer.models.Connection;
import cz.honzamrazek.sensorstreamer.util.TaggedString;

public class EditConnectionActivity extends AppCompatActivity
        implements SpecialisedConnectionInterface.OnFragmentInteractionListener
{
    public final static String EXTRA_CONNECTION = "cz.honzamrazek.cz.sensorstreamer.CONNECTION_IDX";
    private Connection mConnection;
    private EditText mNameEditText;
    private Button mSaveButton;
    private SharedStorageManager<Connection> mConnectionManager;
    private SpecialisedConnectionInterface mConnectionDetailFragment;
    private boolean mNameEdited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_connection);

        mNameEdited = false;

        mConnectionManager = new SharedStorageManager<Connection>(this, Connection.class);

        Intent intent = getIntent();
        int connectionPos = intent.getIntExtra(EXTRA_CONNECTION, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        if (connectionPos == -1) {
            ab.setTitle(R.string.new_connection);
            mConnection = new Connection();
            mConnectionManager.add(mConnection);
        }
        else {
            ab.setTitle(R.string.edit_connection);
            mConnection = mConnectionManager.get(connectionPos);
        }

        setupSaveButton();
        setupSpinner();
        setupNameField();
    }

    public void setupNameField() {
        mNameEditText = (EditText) findViewById(R.id.name);
        mNameEditText.setText(mConnection.getName(), TextView.BufferType.EDITABLE);
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mNameEdited = true;
                enableSaveButton();
            }
        });
    }

    void setupSaveButton() {
        mSaveButton = (Button) findViewById(R.id.save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
    }

    void setupSpinner() {
        ArrayAdapter<TaggedString<Connection.Type>> adapter
                = new ArrayAdapter<>(this, R.layout.type_spinner_item);
        Resources r = getResources();
        adapter.add(new TaggedString<Connection.Type>(
                r.getString(R.string.choose_type), Connection.Type.Empty));
        adapter.add(new TaggedString<Connection.Type>(
                r.getString(R.string.tcp_server), Connection.Type.TcpServer));
        adapter.add(new TaggedString<Connection.Type>(
                r.getString(R.string.tcp_client), Connection.Type.TcpClient));

        int active = 0;
        for (int i = 0; i != adapter.getCount(); i++) {
            if (adapter.getItem(i).getTag() == mConnection.getType()) {
                active = i;
                break;
            }
        }

        Spinner spinner = (Spinner) findViewById(R.id.type);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TaggedString<Connection.Type> item =
                        (TaggedString<Connection.Type>) parent.getItemAtPosition(position);
                changeConnectionType(item.getTag());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                changeConnectionType(Connection.Type.Empty);
            }
        });
        spinner.setSelection(active);
        changeConnectionType(mConnection.getType());
    }

    public boolean validateName() {
        if (mNameEditText == null)
            return false;
        if (mNameEditText.getText().length() == 0) {
            if(mNameEdited)
                mNameEditText.setError(getString(R.string.name_cannot_be_empty));
            return false;
        }
        mNameEditText.setError(null);
        return true;
    }

    public void enableSaveButton() {
        if (!validateName() || mConnectionDetailFragment == null) {
            mSaveButton.setEnabled(false);
        }
        else {
            mSaveButton.setEnabled(mConnectionDetailFragment.isValid());
        }
    }

    void changeConnectionType(Connection.Type type) {
        mConnection.setType(type);

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (type) {
            case TcpServer:
                fragment = new TcpServerConnectionFragment();
                break;
            case TcpClient:
                fragment = new TcpClientConnectionFragment();
                break;
            default:
                if (mConnectionDetailFragment != null)
                    fragmentManager.beginTransaction().remove((Fragment)mConnectionDetailFragment).commit();
                return;
        }
        fragmentManager.beginTransaction().replace(R.id.details, fragment).commit();
        mConnectionDetailFragment = (SpecialisedConnectionInterface) fragment;

        enableSaveButton();
    }

    void onSave() {
        mConnection.setName(mNameEditText.getText().toString());
        mConnectionDetailFragment.commit(mConnection);
        mConnection.sanitize();
        mConnectionManager.commit();
        finish();
    }

    @Override
    public void onValidation(boolean valid) {
        enableSaveButton();
    }

    @Override
    public Connection onDataLoad() {
        return mConnection;
    }
}
