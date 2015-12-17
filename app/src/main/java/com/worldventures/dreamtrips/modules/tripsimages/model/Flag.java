package com.worldventures.dreamtrips.modules.tripsimages.model;

import com.google.gson.annotations.SerializedName;

public class Flag {
    private int id;

    private String name;

    @SerializedName("require_description")
    private boolean requireDescription;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isRequireDescription() {
        return requireDescription;
    }
}
