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
        protected String statusValue;
        @SerializedName("outage_type")
        protected String outageType;
        @SerializedName("message")
        protected String message;

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
            return statusValue;
        }

        public void setStatus(String status) {
            this.statusValue = status;
        }
    }
}