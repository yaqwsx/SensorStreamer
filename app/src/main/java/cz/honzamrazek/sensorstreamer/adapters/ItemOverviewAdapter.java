package cz.honzamrazek.sensorstreamer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.honzamrazek.sensorstreamer.R;
import cz.honzamrazek.sensorstreamer.models.Descriptionable;

public class ItemOverviewAdapter<T extends Descriptionable> extends ArrayAdapter<T> {
    public ItemOverviewAdapter(Context context, List<T> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T conn = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_overview, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView description = (TextView) convertView.findViewById(R.id.description);

        name.setText(conn.getName());
        description.setText(conn.getDescription());

        return convertView;
    }
}
