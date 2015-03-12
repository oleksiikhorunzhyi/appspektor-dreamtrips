package com.worldventures.dreamtrips.core.model.bucket;

/**
 * Created by 1 on 11.03.15.
 */
public class BucketPostItem {

    private String name;
    private String type;
    private Integer id;
    private String status;

    private transient boolean isLoaded = false;
    private transient boolean isError = false;

    public BucketPostItem() {
    }

    public BucketPostItem(String type, Integer id, String status) {
        this("", type, id, status);
    }

    public BucketPostItem(String type, String name, String status) {
        this(name, type, null, status);
    }

    public BucketPostItem(String name, String type, Integer id, String status) {
        this.name = name;
        this.type = type.toLowerCase();
        this.id = id;
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean isError) {
        this.isError = isError;
    }
}
