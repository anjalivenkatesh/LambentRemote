package com.lambent.lambentremote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lambent.lambentremote.ProgramFetchNetworkFragment.FetchListener;
import com.lambent.lambentremote.ProgramFetchNetworkFragment.ServiceDiscoveryListener;
import com.lambent.lambentremote.model.ProgramListResponse;

import rxbonjour.model.BonjourService;

public class MainActivity extends AppCompatActivity implements FetchListener, ServiceDiscoveryListener {

    private TextView discoverButton;
    private LinearLayout servicesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        assignListeners();

        if (getNetworkFragment() == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(new ProgramFetchNetworkFragment(), ProgramFetchNetworkFragment.TAG)
                    .commit();
        }
    }

    private void findViews() {
        discoverButton = (TextView) findViewById(R.id.discoverButton);
        servicesLayout = (LinearLayout) findViewById(R.id.servicesLayout);
    }

    private void assignListeners() {
        discoverButton.setOnClickListener(v -> discoverServices());
    }

    private ProgramFetchNetworkFragment getNetworkFragment() {
        return (ProgramFetchNetworkFragment) getSupportFragmentManager().findFragmentByTag(ProgramFetchNetworkFragment.TAG);
    }

    private void discoverServices() {
        servicesLayout.removeAllViews();
        getNetworkFragment().discoverServices();
    }

    private void addServiceTextView(BonjourService serviceInfo) {
        runOnUiThread(() -> {
            LayoutInflater layoutInflater = getLayoutInflater();

            TextView serviceTextView = (TextView) layoutInflater.inflate(R.layout.service_textview, null);
            serviceTextView.setText("Service found: " + serviceInfo.getName() + " " + serviceInfo.getType() + " " + serviceInfo.getHost() + " " + serviceInfo.getPort());

            servicesLayout.addView(serviceTextView);
        });
    }

    @Override
    public void onProgramListFetched(ProgramListResponse programListResponse, String hostAddress, int port) {
        //TODO
    }

    @Override
    public void onProgramListFetchFailed(String hostAddress, int port) {
        //TODO dialog to retry
    }

    @Override
    public void onServiceDiscovered(BonjourService serviceInfo) {
        addServiceTextView(serviceInfo);
    }

    @Override
    public void onServiceFailed() {
        //TODO dialog to retry
    }
}
