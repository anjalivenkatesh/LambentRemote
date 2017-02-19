package com.lambent.lambentremote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class ProgramListResponse implements Parcelable {

    public static final Creator<ProgramListResponse> CREATOR = new Creator<ProgramListResponse>() {
        @Override
        public ProgramListResponse createFromParcel(Parcel in) {
            return new ProgramListResponse(in);
        }

        @Override
        public ProgramListResponse[] newArray(int size) {
            return new ProgramListResponse[size];
        }
    };

    @SerializedName("available_grouped")
    private final HashMap<String, List<LambentProgram>> programs;

    public ProgramListResponse() {
        programs = null;
    }

    protected ProgramListResponse(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public HashMap<String, List<LambentProgram>> getPrograms() {
        return programs;
    }
}
