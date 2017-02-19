package com.lambent.lambentremote;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by anjali on 10/20/16.
 */
public class ProgramFetchService extends Service {

    private static final String EXTRA_FETCH_PROGRAMS = "ProgramFetchService.EXTRA_FETCH_PROGRAMS";

    public static void fetchPrograms(Context context) {
        Intent intent = new Intent(context, ProgramFetchService.class);
        intent.putExtra(EXTRA_FETCH_PROGRAMS, true);

        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(EXTRA_FETCH_PROGRAMS)) {

        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void fetchProgramsInternal() {

    }
}
