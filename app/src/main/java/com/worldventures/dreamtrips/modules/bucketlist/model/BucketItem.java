package com.worldventures.dreamtrips.modules.bucketlist.model;

import android.support.annotation.StringRes;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class BucketItem extends BaseFeedEntity {
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
   private transient String translationDescription;

   @Override
   public String place() {
      return location != null ? location.getName() : null;
   }

   public String getName() {
      return name;
   }

   public String getStatus() {
      return status;
   }

   public Date getTargetDate() {
      return targetDate;
   }

   public Date getCompletionDate() {
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
      if (photos == null) {
         photos = new ArrayList<>();
      }
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
      if (friends != null) {
         return Queryable.from(friends).joinStrings(", ");
      } else {
         return "";
      }
   }

   public List<String> getFriendsList() {
      return friends;
   }

   public String getBucketTags() {
      if (tags != null) {
         return Queryable.from(tags).joinStrings(", ", BucketTag::getName);
      } else {
         return "";
      }
   }

   public List<BucketTag> getTags() {
      return tags;
   }


   public void setName(String name) {
      this.name = name;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setTargetDate(Date targetDate) {
      this.targetDate = targetDate;
   }

   public void setCompletionDate(Date completionDate) {
      this.completionDate = completionDate;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setTags(List<BucketTag> tags) {
      this.tags = tags;
   }

   public void setCategory(CategoryItem category) {
      this.category = category;
   }

   public void setFriends(List<String> friends) {
      this.friends = friends;
   }

   public void setPhotos(List<BucketPhoto> photos) {
      this.photos = photos;
   }

   public void setLocation(BucketLocation location) {
      this.location = location;
   }

   public void setLink(String link) {
      this.link = link;
   }

   public void setDining(DiningItem dining) {
      this.dining = dining;
   }

   @Override
   public String getOriginalText() {
      return name;
   }

   public String getTranslationDescription() {
      return translationDescription;
   }

   public void setTranslationDescription(String translationDescription) {
      this.translationDescription = translationDescription;
   }

   public enum BucketType {
      LOCATION("location", R.string.bucket_locations),
      ACTIVITY("activity", R.string.bucket_activities),
      DINING("dining", R.string.bucket_restaurants);

      protected String name;
      protected int res;

      BucketType(String name, @StringRes int res) {
         this.name = name;
         this.res = res;
      }

      public String getName() {
         return name;
      }

      public String getAnalyticsName() {
         switch (this) {
            case LOCATION:
               return BucketListModule.ANALYTICS_LOCATIONS;
            case ACTIVITY:
               return BucketListModule.ANALYTICS_ACTIVITIES;
            case DINING:
               return BucketListModule.ANALYTICS_DINING;
            default:
               return "";
         }
      }

      @StringRes
      public int getRes() {
         return res;
      }

   }
}
