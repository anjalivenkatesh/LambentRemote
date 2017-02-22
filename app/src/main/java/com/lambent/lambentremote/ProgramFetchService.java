package com.lambent.lambentremote;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.lambent.lambentremote.common.RetrofitServicesFactory;
import com.lambent.lambentremote.model.ProgramListResponse;
import com.lambent.lambentremote.networking.ProgramListRetrofitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rxbonjour.RxBonjour;
import rxbonjour.model.BonjourService;

/**
 * Created by anjali on 10/20/16.
 */
public class ProgramFetchService extends Service {

    private static final String ACTION_PROGRAM_BROADCAST = "ProgramFetchService.ACTION_PROGRAM_BROADCAST";
    private static final String EXTRA_FETCHING_PROGRAMS_LISTS = "ProgramFetchService.EXTRA_FETCH_PROGRAMS_LISTS";
    private static final String EXTRA_PROGRAM = "ProgramFetchService.EXTRA_PROGRAMS";
    private static final String EXTRA_PROGRAM_LIST = "ProgramFetchService.EXTRA_PROGRAM_LIST";
    private static final String EXTRA_ERROR = "ProgramFetchService.EXTRA_ERROR";

    private static final String TAG = "ProgramFetchService.TAG";
    private static final String EXTRA_DISCOVER_SERVICES = "ProgramFetchService.EXTRA_DISCOVER_SERVICES";
    private static final String EXTRA_DISCOVER_SERVICES_OR_BROADCAST = "ProgramFetchService.EXTRA_DISCOVER_SERVICES_OR_BROADCAST";

    private static final String SERVICE_TYPE = "_aether._tcp.local.";

    private CompositeSubscription subscriptions = new CompositeSubscription();
    private Subscription serviceDiscoverySubscription;

    private Map<String, ProgramListResponse> programListMap = new HashMap<>();
    private String error;

    public static void discoverServices(Context context) {
        Intent intent = new Intent(context, ProgramFetchService.class);
        intent.putExtra(EXTRA_DISCOVER_SERVICES, true);

        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra(EXTRA_DISCOVER_SERVICES)) {
                discoverServicesInternal();
            } else if (intent.hasExtra(EXTRA_DISCOVER_SERVICES_OR_BROADCAST)) {
                if (programListMap.isEmpty() || error == null) {
                    discoverServicesInternal();
                } else {
                    broadcastCurrentState();
                }
            }
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void discoverServicesInternal() {
        releaseSubscriptions();
        programListMap.clear();
        error = null;

        broadcastLoading();
        serviceDiscoverySubscription = RxBonjour.newDiscovery(this, SERVICE_TYPE)
                .subscribe(bonjourEvent -> {
                    BonjourService item = bonjourEvent.getService();
                    switch (bonjourEvent.getType()) {
                        case ADDED:
                            // Called when a service was discovered
                            fetchProgramListInternal(item);
                            break;

                        case REMOVED:
                            // Called when a service is no longer visible
                            String key = generateKey(item.getHost().getHostAddress(), item.getPort());
                            if (!programListMap.containsKey(key)) {
                                programListMap.remove(key);
                            }
                            break;
                    }
                }, error -> {
                    this.error = error.getLocalizedMessage();
                    broadcastError();
                });
    }

    private void fetchProgramListInternal(BonjourService service) {
        String hostAddress = service.getHost().getHostAddress();
        int port = service.getPort();

        ProgramListRetrofitService programListService = RetrofitServicesFactory.newService(ProgramListRetrofitService.class, hostAddress, port);

        Subscription programListSubscription = programListService.getProgramList().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProgramListResponse>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        ProgramFetchService.this.error = e.getLocalizedMessage();
                        broadcastError();
                    }

                    @Override
                    public void onNext(ProgramListResponse programListResponse) {
                        programListMap.put(generateKey(hostAddress, port), programListResponse);
                        broadcastProgramListResponse(programListResponse);
                    }
                });

        subscriptions.add(programListSubscription);
    }

    private String generateKey(String hostAddress, int port) {
        return hostAddress + ":" + port;
    }

    private void broadcastLoading() {
        Intent intent = new Intent(ACTION_PROGRAM_BROADCAST);
        intent.putExtra(EXTRA_FETCHING_PROGRAMS_LISTS, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastProgramListResponse(ProgramListResponse programListResponse) {
        Intent intent = new Intent(ACTION_PROGRAM_BROADCAST);
        intent.putExtra(EXTRA_PROGRAM, programListResponse);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastError() {
        Intent intent = new Intent(ACTION_PROGRAM_BROADCAST);
        intent.putExtra(EXTRA_ERROR, error);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastCurrentState() {
        Intent intent = new Intent(ACTION_PROGRAM_BROADCAST);

        if (!programListMap.isEmpty()) {
            ArrayList<ProgramListResponse> list = new ArrayList<>(programListMap.values());
            intent.putParcelableArrayListExtra(EXTRA_PROGRAM_LIST, list);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void releaseSubscriptions() {
        if (serviceDiscoverySubscription != null) {
            serviceDiscoverySubscription.unsubscribe();
        }

        subscriptions.unsubscribe();
        serviceDiscoverySubscription = null;
    }
}
