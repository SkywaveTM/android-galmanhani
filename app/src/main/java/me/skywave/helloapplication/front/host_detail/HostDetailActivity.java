package me.skywave.helloapplication.front.host_detail;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import me.skywave.helloapplication.R;
import me.skywave.helloapplication.utils.local_storage.HostInformation;
import me.skywave.helloapplication.utils.local_storage.HostStorage;

public class HostDetailActivity extends AppCompatActivity {
    private String hostId;
    private HostDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        hostId = getIntent().getStringExtra("host_id");
        HostStorage storage = new HostStorage(this);
        HostInformation hostInformation = storage.getHost(hostId);

        if (hostInformation == null) {
            finish();
            return;
        }

        setTitle(String.format("%s Information", hostInformation.getName()));

        fragment = HostDetailFragment.newInstance(hostId);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.root_view, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            fragment.refresh();
        }

        return super.onOptionsItemSelected(item);
    }
}
