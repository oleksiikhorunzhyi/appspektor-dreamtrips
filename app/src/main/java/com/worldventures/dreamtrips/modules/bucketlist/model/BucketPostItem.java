package com.worldventures.dreamtrips.modules.bucketlist.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BucketPostItem {
    private Integer id;
    private String name;
    private String type;
    private String status;
    private String date;
    private String description;
    private List<String> tags;
    private List<String> people;

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

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setPeople(List<String> people) {
        this.people = people;
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

    public void setStatus(boolean status) {
        this.status = status ? BucketItem.COMPLETED : BucketItem.NEW;
    }

    public void setDate(Date date) {
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
