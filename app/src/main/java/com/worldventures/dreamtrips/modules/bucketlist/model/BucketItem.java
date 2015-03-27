package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.queriables.Queryable;
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

    @Deprecated
    @TaggedFieldSerializer.Tag(7)
    private List<BucketTag> bucketTags;

    @TaggedFieldSerializer.Tag(8)
    private List<BucketTag> tags;

    @TaggedFieldSerializer.Tag(9)
    private CategoryItem categoryItem;

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public Date getTarget_date() {
        return target_date;
    }

    public Date getCompletion_date() {
        return completion_date;
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

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return categoryItem != null ? categoryItem.getName() : "";
    }

    public String getBucketTags() {
        return tags != null
                ? Queryable.from(tags).joinStrings(", ", (element) -> element.getName())
                : "";
    }
}
