package me.skywave.helloapplication.front.host_detail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.skywave.helloapplication.R;
import me.skywave.helloapplication.utils.server_api.HostDensityData;
import me.skywave.helloapplication.utils.server_api.ServerApi;
import me.skywave.helloapplication.front.dialog.HostEditDialog;

public class HostDetailFragment extends Fragment {
    private static final String HOST_ID_PARAM = "host_id";
    private String hostId;
    private ServerApi api;
    private ListView listView;
    private HostDensityAdapter adapter;
    private TextView countText;
    private TextView dateText;

    private ListRefreshTask refreshTask = null;

    public static HostDetailFragment newInstance(String hostId) {
        HostDetailFragment fragment = new HostDetailFragment();

        Bundle args = new Bundle();
        args.putString(HOST_ID_PARAM, hostId);
        fragment.setArguments(args);

        return fragment;
    }

    public HostDetailFragment() {
        api = new ServerApi();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            hostId = getArguments().getString(HOST_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_host_detail, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HostEditDialog.class);
                intent.putExtra("host_id", hostId);

                startActivity(intent);
            }
        });

        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new HostDensityAdapter(getActivity());
        listView.setAdapter(adapter);

        countText = (TextView) view.findViewById(R.id.count_text);
        dateText = (TextView) view.findViewById(R.id.date_text);

        refresh();

        return view;
    }


    @Override
    public void onDestroy() {
        if (refreshTask != null) {
            refreshTask.cancel(false);
        }

        super.onDestroy();
    }

    public void refresh() {
        updateViewContent(null, "Getting Data...", "Please wait");

        if (refreshTask != null) {
            refreshTask.cancel(false);
        }

        refreshTask = new ListRefreshTask();
        refreshTask.execute();
    }

    private synchronized void updateViewContent(final List<HostDensityData> list, final String count, final String date) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();

                if (list != null) {
                    adapter.addAll(list);
                }

                countText.setText(count);
                dateText.setText(date);
            }
        });
    }

    private class ListRefreshTask extends AsyncTask<Void, Void, Void> {
        private String count;
        private String date;
        private List<HostDensityData> datas;

        @Override
        protected void onPostExecute(Void aVoid) {
            updateViewContent(datas, count, date);
        }

        @Override
        protected Void doInBackground(Void... params) {
            datas = api.listHostDensityData(hostId);
            HostDensityData connectionData = api.getHostConnectionData(hostId);

            count = "NO DATA";
            date = "";

            if (connectionData != null) {
                count = String.valueOf(connectionData.connected_count);
                date = connectionData.getRefinedDate();
            }

            return null;
        }
    }
}
