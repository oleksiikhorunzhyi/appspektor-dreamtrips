package com.worldventures.dreamtrips.modules.common.model;

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
        @SerializedName("status")
        String status;
        @SerializedName("outage_type")
        String outageType;
        @SerializedName("message")
        String message;

        public String getOutageType() {
            return outageType;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
