package cz.honzamrazek.sensorstreamer.models;

import android.content.Context;

public interface Descriptionable {
    String getName();
    String getDescription(Context context);
}
