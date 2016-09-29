package com.lambent.lambentremote.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class ProgramListResponse {

    @SerializedName("available_grouped")
    private final HashMap<String, List<LambentProgram>> programs;

    public ProgramListResponse() {
        programs = null;
    }

    public HashMap<String, List<LambentProgram>> getPrograms() {
        return programs;
    }
}
