package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BucketItem implements IFeedObject, Serializable {

    public static final String NEW = "new";
    public static final String COMPLETED = "completed";

    private int id;
    private long uid;
    private String name;
    private String status = NEW;
    @SerializedName("target_date")
    private Date targetDate;
    @SerializedName("completion_date")
    private Date completionDate;
    private String type;
    private String description;
    private List<BucketTag> tags;
    @SerializedName("category")
    private CategoryItem categoryItem;
    private List<String> friends;
    private List<BucketPhoto> photos = Collections.emptyList();
    @SerializedName("cover_photo")
    private BucketPhoto coverPhoto;
    private BucketLocation location;
    private String link;
    private DiningItem dining;

    private List<Comment> comments;
    @SerializedName("comments_count")
    private int commentsCount;

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

    public int getId() {
        return id;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BucketItem that = (BucketItem) o;
        return that.uid == uid;
    }

    @Override
    public int hashCode() {
        return (int) (uid ^ (uid >>> 32));
    }

    ///////////////////////////////////////////
    //////// Feed item
    ///////////////////////////////////////////

    @Override
    public String place() {
        return null;
    }

    @Override
    public long getUid() {
        return uid;
    }

    @Override
    public Date getCreatedAt() {
        return targetDate;
    }

    @Override
    public int commentsCount() {
        return commentsCount;
    }

    @Override
    public List<Comment> getComments() {
        return comments;
    }
}
