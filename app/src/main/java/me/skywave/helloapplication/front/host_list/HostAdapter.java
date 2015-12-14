package me.skywave.helloapplication.front.host_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import me.skywave.helloapplication.R;
import me.skywave.helloapplication.utils.local_storage.HostInformation;

// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class HostAdapter extends ArrayAdapter<HostInformation> {
    private String currentId;

    public HostAdapter(Context context) {
        super(context, 0);
        currentId = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HostInformation host = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.host_list_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.text_view);
        textView.setText(String.format("[%c] %s", host.isAllowed() ? 'O' : 'X', host.getName()));

        if (currentId != null && currentId.equals(host.getHostId())) {
            textView.append(" <CURRENT>");
        }

        return convertView;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

}
