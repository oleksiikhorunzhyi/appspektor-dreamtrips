package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedObject;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BucketItem extends BaseFeedObject implements IFeedObject, Serializable {

    public static final String NEW = "new";
    public static final String COMPLETED = "completed";

    private String name;
    private String status = NEW;
    private Date targetDate;
    private Date completionDate;
    private String type;
    private String description;
    private List<BucketTag> tags;
    private CategoryItem category;
    private List<String> friends;
    private List<BucketPhoto> photos = Collections.emptyList();
    private BucketPhoto coverPhoto;
    private BucketLocation location;
    private String link;
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
        return category;
    }

    public DiningItem getDining() {
        return dining;
    }

    public String getCategoryName() {
        if (category != null) {
            return category.getName();
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
            return Queryable.from(tags).joinStrings(", ", BucketTag::getName);
        } else {
            return "";
        }
    }

}
