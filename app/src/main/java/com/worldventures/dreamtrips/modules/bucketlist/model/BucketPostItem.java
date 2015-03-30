package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.util.Date;
import java.util.List;

public class BucketPostItem {
    private Integer id;
    @SerializedName("category_id")
    private Integer categoryId;
    private String name;
    private String type;
    private String status;
    @SerializedName("target_date")
    private String date;
    private String description;
    private List<String> tags;
    private List<String> friends;

    public BucketPostItem() {
    }

    public BucketPostItem(String type, Integer id) {
        this("", type, id, "");
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
        this.friends = people;
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
        if (status) {
            this.status = BucketItem.COMPLETED;
        } else {
            this.status = BucketItem.NEW;
        }
    }

    public void setCategory(CategoryItem category) {
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public void setDate(Date date) {
        this.date = DateTimeUtils.convertDateToString(date, DateTimeUtils.DEFAULT_ISO_FORMAT);
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
