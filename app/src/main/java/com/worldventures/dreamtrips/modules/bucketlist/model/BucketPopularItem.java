package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

/**
 * Created by 1 on 03.03.15.
 */
public class BucketPopularItem extends BaseEntity {

    private String imageLink;
    private String name;
    private String description;

    private boolean done;
    private boolean add;

    public BucketPopularItem() {
        this.imageLink = "http://upload.wikimedia.org/wikipedia/commons/thumb/9/9d/Golden_Gate_Bridge_.JPG/800px-Golden_Gate_Bridge_.JPG";
        this.name = "Golden Gate Bridge";
        this.description = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    }

    public BucketPopularItem(String imageLink, String name, String description) {
        this.imageLink = imageLink;
        this.name = name;
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }
}
