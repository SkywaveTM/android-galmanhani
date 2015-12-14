package me.skywave.helloapplication.front.host_list;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import me.skywave.helloapplication.R;
import me.skywave.helloapplication.utils.Utils;
import me.skywave.helloapplication.background.BackgroundUtils;
import me.skywave.helloapplication.front.dialog.HostFeedbackDialog;
import me.skywave.helloapplication.front.host_detail.HostDetailActivity;
import me.skywave.helloapplication.utils.local_storage.HostInformation;
import me.skywave.helloapplication.utils.local_storage.HostStorage;

public class HostListFragment extends Fragment {
    private HostStorage hosts;
    private WifiManager wifiManager;


    private TextView textView;
    private Button scanButton;
    private ListView listView;
    private HostAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hosts = new HostStorage(getActivity());
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_list, container, false);

        textView = (TextView) view.findViewById(R.id.text_view);
        scanButton = (Button) view.findViewById(R.id.scan_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        adapter = new HostAdapter(getActivity());
        listView.setAdapter(adapter);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundUtils.scanAndSend(getActivity(), 0);

                Intent intent = new Intent(getActivity(), HostFeedbackDialog.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HostInformation host = adapter.getItem(position);

                Intent intent = new Intent(getActivity(), HostDetailActivity.class);
                intent.putExtra("host_id", host.getHostId());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        String hostId = Utils.getHostId(getActivity());

        String currentName;

        if (Utils.isNoHost(hostId)) {
            currentName = "NOT CONNECTED";
        } else {
            HostInformation host = new HostStorage(getActivity()).getHost(hostId);

            if (host != null) {
                currentName = host.getName();
            } else {
                currentName = "NOT ADDED";
            }

        }

        textView.setText(currentName);

        adapter.clear();
        adapter.setCurrentId(hostId);
        adapter.addAll(hosts.getAllHosts());
    }
}
