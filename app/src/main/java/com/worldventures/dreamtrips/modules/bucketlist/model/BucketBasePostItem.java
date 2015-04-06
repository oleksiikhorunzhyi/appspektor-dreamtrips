package com.worldventures.dreamtrips.modules.bucketlist.model;

public class BucketBasePostItem {
    private Integer id;
    private String type;
    private String status;

    public BucketBasePostItem() {
    }

    public BucketBasePostItem(Integer id, String type, String status) {
        this.id = id;
        this.type = type;
        this.status = status;
    }

    public BucketBasePostItem(String type, Integer id) {
       this(id, type, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

}
