package com.lambent.lambentremote.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anjali on 10/20/16.
 */
public class RemoteLightInstance implements Parcelable {

    private final String hostAddress;
    private final int port;
    private final ProgramListResponse programListResponse;

    public static final Creator<RemoteLightInstance> CREATOR = new Creator<RemoteLightInstance>() {
        @Override
        public RemoteLightInstance createFromParcel(Parcel in) {
            return new RemoteLightInstance(in);
        }

        @Override
        public RemoteLightInstance[] newArray(int size) {
            return new RemoteLightInstance[size];
        }
    };

    public RemoteLightInstance(String hostAddress, int port, ProgramListResponse programListResponse) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.programListResponse = programListResponse;
    }

    protected RemoteLightInstance(Parcel in) {
        hostAddress = in.readString();
        port = in.readInt();
        programListResponse = in.readParcelable(ProgramListResponse.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hostAddress);
        dest.writeInt(port);
        dest.writeParcelable(programListResponse, flags);
    }
}
