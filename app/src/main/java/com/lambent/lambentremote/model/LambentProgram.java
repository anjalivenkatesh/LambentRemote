package com.lambent.lambentremote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LambentProgram implements Parcelable {

    public static final Creator<LambentProgram> CREATOR = new Creator<LambentProgram>() {
        @Override
        public LambentProgram createFromParcel(Parcel in) {
            return new LambentProgram(in);
        }

        @Override
        public LambentProgram[] newArray(int size) {
            return new LambentProgram[size];
        }
    };

    @SerializedName("action")
    private final String action;

    @SerializedName("colors")
    private final ArrayList<String> colors;

    @SerializedName("display")
    private final String display;

    public LambentProgram() {
        action = null;
        colors = null;
        display = null;
    }

    protected LambentProgram(Parcel in) {
        action = in.readString();
        colors = in.createStringArrayList();
        display = in.readString();
    }

    public String getAction() {
        return action;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public String getDisplay() {
        return display;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeStringList(colors);
        dest.writeString(display);
    }
}
