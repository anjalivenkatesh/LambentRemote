package com.lambent.lambentremote.networking;

import com.lambent.lambentremote.model.ProgramListResponse;

import retrofit.http.GET;
import rx.Observable;

public interface ProgramListService {

    @GET("/progs_grp")
    public Observable<ProgramListResponse> getProgramList();
}
