package com.worldventures.dreamtrips.modules.bucketlist.model;

public class BucketBasePostItem {
    private int id;
    private String type;
    private String status;

    public BucketBasePostItem() {
    }

    public BucketBasePostItem(int id, String type, String status) {
        this.id = id;
        this.type = type;
        this.status = status;
    }

    public BucketBasePostItem(String type, int id) {
       this(id, type, BucketItem.NEW);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        if (status) {
            this.status = BucketItem.COMPLETED;
        } else {
            this.status = BucketItem.NEW;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
