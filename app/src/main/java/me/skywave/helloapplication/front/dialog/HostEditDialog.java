package me.skywave.helloapplication.front.dialog;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.skywave.helloapplication.R;
import me.skywave.helloapplication.utils.Utils;
import me.skywave.helloapplication.utils.local_storage.HostInformation;
import me.skywave.helloapplication.utils.local_storage.HostStorage;

public class HostEditDialog extends Activity {
    private EditText nameField;

    private HostStorage storage;
    private boolean doScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_host_dialog);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(getIntent().getIntExtra("noti_id", 0));

        String hostId = getIntent().getStringExtra("host_id");
        if (hostId == null) {
            hostId = Utils.getHostId(this);
        }

        if (Utils.isNoHost(hostId)) {
            Toast.makeText(this, "Not connected.", Toast.LENGTH_SHORT).show();
            finish();
        }

        String hostName;

        storage = new HostStorage(this);
        HostInformation hostInformation = storage.getHost(hostId);
        if (hostInformation == null) {
            setTitle("Add new host");
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            hostName = networkInfo.getExtraInfo();
            hostName = hostName.substring(1, hostName.length() - 1);
        } else {
            setTitle("Edit host");
            hostName = hostInformation.getName();
        }

        doScan = getIntent().getBooleanExtra("do_scan", false);

        TextView textView = (TextView) findViewById(R.id.text_view);
        Button allowButton = (Button) findViewById(R.id.allow_button);
        Button blockButton = (Button) findViewById(R.id.block_button);
        nameField = (EditText) findViewById(R.id.name_field);

        textView.setText("Input location name:");
        nameField.setText(hostName);

        final String finalHostId = hostId;
        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storage.addHost(new HostInformation(finalHostId, nameField.getText().toString(), true));

                if (doScan) {
                    Intent intent = new Intent(HostEditDialog.this, HostFeedbackDialog.class);
                    intent.putExtra("host_id", finalHostId);

                    startActivity(intent);
                }

                finish();
            }
        });

        blockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storage.addHost(new HostInformation(finalHostId, nameField.getText().toString(), false));
                finish();
            }
        });
    }
}
