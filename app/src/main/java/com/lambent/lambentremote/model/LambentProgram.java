package com.lambent.lambentremote.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LambentProgram {

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

    public String getAction() {
        return action;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public String getDisplay() {
        return display;
    }
}
