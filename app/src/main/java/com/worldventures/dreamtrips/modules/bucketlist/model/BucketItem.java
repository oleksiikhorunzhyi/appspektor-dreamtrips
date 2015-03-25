package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.util.Date;
import java.util.List;

@DefaultSerializer(TaggedFieldSerializer.class)
public class BucketItem extends BaseEntity {

    public static final String NEW = "new";
    public static final String COMPLETED = "completed";

    @TaggedFieldSerializer.Tag(1)
    private String name;

    @TaggedFieldSerializer.Tag(2)
    private String status = NEW;

    @TaggedFieldSerializer.Tag(3)
    private Date target_date;

    @TaggedFieldSerializer.Tag(4)
    private Date completion_date;

    @TaggedFieldSerializer.Tag(5)
    private String type;

    @TaggedFieldSerializer.Tag(6)
    private String description;

    @TaggedFieldSerializer.Tag(7)
    private List<BucketTag> bucketTags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTarget_date() {
        return target_date;
    }

    public void setTarget_date(Date target_date) {
        this.target_date = target_date;
    }

    public Date getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(Date completion_date) {
        this.completion_date = completion_date;
    }

    public boolean isDone() {
        return status.equals(COMPLETED);
    }

    public void setDone(boolean status) {
        this.status = status ? COMPLETED : NEW;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<BucketTag> getBucketTags() {
        return bucketTags;
    }

    public void setBucketTags(List<BucketTag> bucketTags) {
        this.bucketTags = bucketTags;
    }
}
