package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.util.Collections;
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
    @SerializedName("target_date")
    private Date targetDate;

    @TaggedFieldSerializer.Tag(4)
    @SerializedName("completion_date")
    private Date completionDate;

    @TaggedFieldSerializer.Tag(5)
    private String type;

    @TaggedFieldSerializer.Tag(6)
    private String description;

    @TaggedFieldSerializer.Tag(7)
    private List<BucketTag> tags;

    @TaggedFieldSerializer.Tag(8)
    @SerializedName("category")
    private CategoryItem categoryItem;

    @TaggedFieldSerializer.Tag(9)
    private List<String> friends;

    @TaggedFieldSerializer.Tag(11)
    private List<BucketPhoto> photos = Collections.emptyList();

    @TaggedFieldSerializer.Tag(12)
    @SerializedName("cover_photo")
    private BucketPhoto coverPhoto;

    @TaggedFieldSerializer.Tag(13)
    private BucketLocation location;

    @TaggedFieldSerializer.Tag(14)
    private String link;

    @TaggedFieldSerializer.Tag(15)
    private DiningItem dining;

    private transient boolean selected;

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public Date getTarget_date() {
        return targetDate;
    }

    public Date getCompletion_date() {
        return completionDate;
    }

    public BucketLocation getLocation() {
        return location;
    }

    public boolean isDone() {
        return status.equals(COMPLETED);
    }

    public void setDone(boolean status) {
        if (status) {
            this.status = BucketItem.COMPLETED;
        } else {
            this.status = BucketItem.NEW;
        }
    }

    public String getUrl() {
        return link;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<BucketPhoto> getPhotos() {
        return photos;
    }

    public void setImages(List<BucketPhoto> images) {
        this.photos = images;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public CategoryItem getCategory() {
        return categoryItem;
    }

    public DiningItem getDining() {
        return dining;
    }

    public String getCategoryName() {
        if (categoryItem != null) {
            return categoryItem.getName();
        } else {
            return "";
        }
    }

    public String getCoverUrl(int w, int h) {
        if (coverPhoto != null) {
            return coverPhoto.getFSImage().getUrl(w, h);
        } else if (getPhotos() != null && !getPhotos().isEmpty()) {
            return getPhotos().get(0).getFSImage().getUrl(w, h);
        } else {
            return "";
        }
    }

    public BucketPhoto getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(BucketPhoto coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public BucketPhoto getFirstPhoto() {
        return Queryable.from(photos).firstOrDefault();
    }

    public String getFriends() {
        if (tags != null) {
            return Queryable.from(friends).joinStrings(", ");
        } else {
            return "";
        }
    }

    public String getBucketTags() {
        if (tags != null) {
            return Queryable.from(tags).joinStrings(", ", (element) -> element.getName());
        } else {
            return "";
        }
    }
}
