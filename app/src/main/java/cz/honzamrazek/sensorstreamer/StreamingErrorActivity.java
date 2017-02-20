package cz.honzamrazek.sensorstreamer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StreamingErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_error);

        setTitle(getIntent().getStringExtra("title"));
        TextView message = (TextView) findViewById(R.id.message);
        message.setText(getIntent().getStringExtra("message"), TextView.BufferType.NORMAL);
    }

    public void onDismiss(View v) {
        finish();
    }
}
