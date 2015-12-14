package me.skywave.helloapplication.front.host_detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import me.skywave.helloapplication.R;
import me.skywave.helloapplication.utils.server_api.HostDensityData;

public class HostDensityAdapter extends ArrayAdapter<HostDensityData> {
    public HostDensityAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HostDensityData host = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.host_density_item, parent, false);
        }

        TextView dataText = (TextView) convertView.findViewById(R.id.data_text);
        TextView dateText = (TextView) convertView.findViewById(R.id.date_text);
        String density;

        switch (host.density) {
            case 1:
                density = "LOW";
                break;
            case 2:
                density = "MIDDLE";
                break;
            case 3:
                density = "HIGH";
                break;
            default:
                density = "UNKNOWN";
        }

        dataText.setText(String.format("connection: %d, density: %s", host.connected_count, density));
        dateText.setText(host.getRefinedDate());

        return convertView;
    }
}
