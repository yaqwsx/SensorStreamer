package cz.honzamrazek.sensorstreamer;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import cz.honzamrazek.sensorstreamer.models.Connection;
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
    }

    public void setupNameField() {
        mNameEditText = (EditText) findViewById(R.id.name);
        mNameEditText.setText(mPacket.getName(), TextView.BufferType.EDITABLE);
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
                changeConnectionType(item.getTag());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                changeConnectionType(Packet.Type.Empty);
            }
        });
        spinner.setSelection(active);
        changeConnectionType(mPacket.getType());
    }

    void changeConnectionType(Packet.Type type) {
        mPacket.setType(type);

        if (type == Packet.Type.Empty) {
            mSaveButton.setEnabled(false);
        }
        else {
            mSaveButton.setEnabled(true);
        }
    }

    void onSave() {
        mPacket.setName(mNameEditText.getText().toString());
        mPacketManager.commit();
        finish();
    }
}
