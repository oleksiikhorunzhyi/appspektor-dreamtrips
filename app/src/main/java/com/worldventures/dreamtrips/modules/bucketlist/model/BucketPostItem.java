package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;

import java.util.Date;
import java.util.List;

public class BucketPostItem  extends BucketBasePostItem {
    @SerializedName("category_id")
    private Integer categoryId;
    private String name;
    @SerializedName("target_date")
    private String date;
    private String description;
    private List<String> tags;
    private List<String> friends;

    public BucketPostItem() {
    }

    public BucketPostItem(String type, String id) {
        this("", type, id, "");
    }

    public BucketPostItem(String type, String name, String status) {
        this(name, type, null, status);
    }

    public BucketPostItem(String name, String type, String id, String status) {
        super(id, type.toLowerCase(), status);
        this.name = name;
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
