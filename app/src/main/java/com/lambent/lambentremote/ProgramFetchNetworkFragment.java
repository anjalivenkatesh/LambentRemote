package com.lambent.lambentremote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.lambent.lambentremote.common.RetrofitServicesFactory;
import com.lambent.lambentremote.model.ProgramListResponse;
import com.lambent.lambentremote.networking.ProgramListRetrofitService;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rxbonjour.RxBonjour;
import rxbonjour.model.BonjourService;

public class ProgramFetchNetworkFragment extends Fragment {

    public static final String TAG = "ProgramFetchNetworkFragment.TAG";

    private static final String SERVICE_TYPE = "_aether._tcp.local.";

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private FetchListener fetchListener;
    private ServiceDiscoveryListener serviceDiscoveryListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.fetchListener = (FetchListener) context;
        this.serviceDiscoveryListener = (ServiceDiscoveryListener) context;
    }

    public void discoverServices() {
        Subscription subscription = RxBonjour.newDiscovery(getActivity(), SERVICE_TYPE)
                .subscribe(bonjourEvent -> {
                    BonjourService item = bonjourEvent.getService();
                    switch (bonjourEvent.getType()) {
                        case ADDED:
                            // Called when a service was discovered
                            serviceDiscoveryListener.onServiceDiscovered(item);
                            fetchProgramList(item);
                            break;

                        case REMOVED:
                            // Called when a service is no longer visible
                            break;
                    }
                }, error -> {
                    serviceDiscoveryListener.onServiceFailed();
                });

        compositeSubscription.add(subscription);
    }

    private void fetchProgramList(BonjourService service) {
        String hostAddress = service.getHost().getHostAddress();
        int port = service.getPort();

        ProgramListRetrofitService programListService = RetrofitServicesFactory.newService(ProgramListRetrofitService.class, hostAddress, port);
        Observable<ProgramListResponse> programListResponseObservable = programListService.getProgramList();

        Subscription programListSubscription = programListResponseObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProgramListResponse>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        //TODO
                        throw new RuntimeException(e);
                    }

                    @Override
                    public void onNext(ProgramListResponse programListResponse) {
                        for (String key : programListResponse.getPrograms().keySet()) {
                            fetchListener.onProgramListFetched(programListResponse, hostAddress, port);
                            System.out.println("Program name: "+key);
                        }
                    }
                });

        compositeSubscription.add(programListSubscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public interface FetchListener {
        void onProgramListFetched(ProgramListResponse programListResponse, String hostAddress, int port);
        void onProgramListFetchFailed(String hostAddress, int port);
    }

    public interface ServiceDiscoveryListener {
        void onServiceDiscovered(BonjourService serviceInfo);
        void onServiceFailed();
    }
}
