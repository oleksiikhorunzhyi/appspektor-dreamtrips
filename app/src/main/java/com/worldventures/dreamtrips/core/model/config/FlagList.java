package com.worldventures.dreamtrips.core.model.config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FlagList {
    @SerializedName("default")
    private List<Flag> items;

    public List<Flag> getDefault() {
        return this.items;
    }

    public void setDefault(List<Flag> items) {
        this.items = items;
    }
}
