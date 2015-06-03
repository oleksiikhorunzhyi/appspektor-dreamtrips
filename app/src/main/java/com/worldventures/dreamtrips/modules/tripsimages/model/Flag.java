package com.worldventures.dreamtrips.modules.tripsimages.model;

import com.google.gson.annotations.SerializedName;

public class Flag {
    private String name;

    @SerializedName("require_description")
    private boolean requireDescription;

    public String getName() {
        return name;
    }

    public boolean isRequireDescription() {
        return requireDescription;
    }
}
