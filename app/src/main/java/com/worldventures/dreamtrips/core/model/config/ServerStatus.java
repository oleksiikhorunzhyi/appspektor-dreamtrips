package com.worldventures.dreamtrips.core.model.config;

import com.google.gson.annotations.SerializedName;

public class ServerStatus {
    @SerializedName("QA")
    private Status qA;
    @SerializedName("production")
    private Status production;

    public Status getQA() {
        return this.qA;
    }

    public void setQA(Status qA) {
        this.qA = qA;
    }

    public Status getProduction() {
        return this.production;
    }

    public void setProduction(Status production) {
        this.production = production;
    }


    public static class Status {
        String status;
        String outage_type;
        String message;
    }
}
