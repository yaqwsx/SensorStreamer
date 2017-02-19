package cz.honzamrazek.sensorstreamer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.SharedStorageManager;
import cz.honzamrazek.sensorstreamer.models.Packet;
import cz.honzamrazek.sensorstreamer.util.TaggedString;

public class EditPacketActivity extends AppCompatActivity {
    public final static String EXTRA_PACKET = "cz.honzamrazek.cz.sensorstreamer.PACKET_IDX";
    private Packet mPacket;
    private EditText mNameEditText;
    private Button mSaveButton;
    private SharedStorageManager<Packet> mPacketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_packet);

        mPacketManager = new SharedStorageManager<Packet>(this, Packet.class);

        Intent intent = getIntent();
        int connectionPos = intent.getIntExtra(EXTRA_PACKET, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        if (connectionPos == -1) {
            ab.setTitle(R.string.new_packet);
            mPacket = new Packet();
            mPacketManager.add(mPacket);
        }
        else {
            ab.setTitle(R.string.edit_packet);
            mPacket = mPacketManager.get(connectionPos);
        }

        setupSaveButton();
        setupSpinner();
        setupNameField();
        setupCheckboxes();
    }

    private void setupCheckboxes() {
        SensorManager sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        CheckBox c = (CheckBox) findViewById(R.id.accelerometer);
        if (sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.accelerometer);

        c = (CheckBox) findViewById(R.id.ambient_temperature);
        if (sManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.ambientTemperature);

        c = (CheckBox) findViewById(R.id.gravity);
        if (sManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.gravity);

        c = (CheckBox) findViewById(R.id.gyroscope);
        if (sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.gyroscope);

        c = (CheckBox) findViewById(R.id.light);
        if (sManager.getDefaultSensor(Sensor.TYPE_LIGHT) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.light);

        c = (CheckBox) findViewById(R.id.linear_acceleration);
        if (sManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.linearAcceleration);

        c = (CheckBox) findViewById(R.id.magnetometer);
        if (sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.magneticField);

        c = (CheckBox) findViewById(R.id.pressure);
        if (sManager.getDefaultSensor(Sensor.TYPE_PRESSURE) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.pressure);

        c = (CheckBox) findViewById(R.id.proximity);
        if (sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.proximity);

        c = (CheckBox) findViewById(R.id.humidity);
        if (sManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.relativeHumidity);

        c = (CheckBox) findViewById(R.id.rotation_vector);
        if (sManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            c.setEnabled(false);
        }
        c.setChecked(mPacket.rotationVector);

        c = (CheckBox) findViewById(R.id.timestamp);
        c.setChecked(mPacket.timeStamp);
    }

    public void setupNameField() {
        mNameEditText = (EditText) findViewById(R.id.name);
        mNameEditText.setText(mPacket.getName(), TextView.BufferType.EDITABLE);
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validate();
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
        ArrayAdapter<TaggedString<Packet.Type>> adapter
                = new ArrayAdapter<>(this, R.layout.type_spinner_item);
        Resources r = getResources();
        adapter.add(new TaggedString<Packet.Type>(
                r.getString(R.string.choose_type), Packet.Type.Empty));
        adapter.add(new TaggedString<Packet.Type>(
                r.getString(R.string.json), Packet.Type.JSON));
        adapter.add(new TaggedString<Packet.Type>(
                r.getString(R.string.binary), Packet.Type.Binary));

        int active = 0;
        for (int i = 0; i != adapter.getCount(); i++) {
            if (adapter.getItem(i).getTag() == mPacket.getType()) {
                active = i;
                break;
            }
        }

        Spinner spinner = (Spinner) findViewById(R.id.type);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TaggedString<Packet.Type> item =
                        (TaggedString<Packet.Type>) parent.getItemAtPosition(position);
                mPacket.setType(item.getTag());
                validate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPacket.setType(Packet.Type.Empty);
                validate();
            }
        });
        spinner.setSelection(active);
        validate();
    }

    void onSave() {
        mPacket.setName(mNameEditText.getText().toString());

        CheckBox c = (CheckBox) findViewById(R.id.accelerometer);
        mPacket.accelerometer = c.isChecked();

        c = (CheckBox) findViewById(R.id.ambient_temperature);
        mPacket.ambientTemperature = c.isChecked();

        c = (CheckBox) findViewById(R.id.gravity);
        mPacket.gravity = c.isChecked();

        c = (CheckBox) findViewById(R.id.gyroscope);
        mPacket.gyroscope = c.isChecked();

        c = (CheckBox) findViewById(R.id.light);
        mPacket.light = c.isChecked();

        c = (CheckBox) findViewById(R.id.linear_acceleration);
        mPacket.linearAcceleration = c.isChecked();

        c = (CheckBox) findViewById(R.id.magnetometer);
        mPacket.magneticField = c.isChecked();

        c = (CheckBox) findViewById(R.id.pressure);
        mPacket.pressure = c.isChecked();

        c = (CheckBox) findViewById(R.id.proximity);
        mPacket.proximity = c.isChecked();

        c = (CheckBox) findViewById(R.id.humidity);
        mPacket.relativeHumidity = c.isChecked();

        c = (CheckBox) findViewById(R.id.rotation_vector);
        mPacket.rotationVector = c.isChecked();

        c = (CheckBox) findViewById(R.id.timestamp);
        mPacket.timeStamp = c.isChecked();

        mPacketManager.commit();
        finish();
    }

    private boolean validate() {
        if (mNameEditText == null || mSaveButton == null)
            return false;
        if (mNameEditText.getText().length() == 0) {
            mNameEditText.setError(getString(R.string.name_cannot_be_empty));
            mSaveButton.setEnabled(false);
            return false;
        }
        if (mPacket.getType() == Packet.Type.Empty) {
            mSaveButton.setEnabled(false);
            return false;
        }
        mSaveButton.setEnabled(true);
        return true;
    }
}
