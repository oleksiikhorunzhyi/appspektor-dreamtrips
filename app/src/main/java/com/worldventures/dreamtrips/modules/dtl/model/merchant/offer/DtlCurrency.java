package com.worldventures.dreamtrips.modules.dtl.model.merchant.offer;

import com.google.gson.annotations.SerializedName;

public class DtlCurrency {

    private String code;
    private String prefix;
    private String suffix;
    private String name;
    @SerializedName("default")
    private boolean isDefault;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
