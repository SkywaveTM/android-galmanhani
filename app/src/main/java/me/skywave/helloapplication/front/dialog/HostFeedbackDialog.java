package me.skywave.helloapplication.front.dialog;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.skywave.helloapplication.R;
import me.skywave.helloapplication.utils.Utils;
import me.skywave.helloapplication.utils.local_storage.HostInformation;
import me.skywave.helloapplication.utils.local_storage.HostStorage;
import me.skywave.helloapplication.background.BackgroundUtils;

public class HostFeedbackDialog extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_density_dialog);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(getIntent().getIntExtra("noti_id", 0));

        String hostId = Utils.getHostId(this);
        HostInformation hostInformation = new HostStorage(this).getHost(hostId);

        if (hostInformation == null) {
            Intent intent = new Intent(this, HostEditDialog.class);
            intent.putExtra("do_scan", true);

            startActivity(intent);

            finish();
            return;
        }

        if (!hostInformation.isAllowed()) {
            Toast.makeText(this, "This host is blocked by the user.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText(String.format("How many people are in '%s'?", hostInformation.getName()));

        setTitle(String.format("Feedback %s", hostInformation.getName()));

        Button lowButton = (Button) findViewById(R.id.low_button);
        Button middleButton = (Button) findViewById(R.id.middle_button);
        Button highButton = (Button) findViewById(R.id.high_button);

        lowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAndFinish(1);
            }
        });

        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAndFinish(2);
            }
        });

        highButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAndFinish(3);
            }
        });
    }

    private void sendAndFinish(int density) {
        BackgroundUtils.scanAndSend(this, density);
        finish();
    }
}
