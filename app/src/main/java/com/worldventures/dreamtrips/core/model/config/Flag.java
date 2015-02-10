package com.worldventures.dreamtrips.core.model.config;

import com.google.gson.annotations.SerializedName;

public class Flag {
    @SerializedName("Code")
    private String code;
    @SerializedName("Description")
    private boolean description;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isNeedDescription() {
        return this.description;
    }

    public void setDescription(boolean description) {
        this.description = description;
    }
}
