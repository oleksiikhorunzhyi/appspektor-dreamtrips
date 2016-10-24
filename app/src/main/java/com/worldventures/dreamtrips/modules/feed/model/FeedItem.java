package com.worldventures.dreamtrips.modules.feed.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FeedItem<T extends FeedEntity> extends BaseEntity implements FeedEntityHolder, TranslatableItem {

   @SerializedName("notification_id") protected int notificationId;
   protected FeedItem.Type type = Type.UNDEFINED;
   protected FeedItem.Action action;
   protected T item;
   protected Links links;
   @SerializedName("posted_at") protected Date createdAt;
   protected Date readAt;

   protected transient String translation;
   protected transient boolean translated;

   private MetaData metaData;

   public static FeedItem create(FeedEntity item, User owner) {
      Type type;
      FeedItem feedItem;
      if (item instanceof TextualPost) {
         feedItem = new PostFeedItem();
         type = Type.POST;
      } else if (item instanceof Photo) {
         feedItem = new PhotoFeedItem();
         type = Type.PHOTO;
      } else if (item instanceof BucketItem) {
         feedItem = new BucketFeedItem();
         type = Type.BUCKET_LIST_ITEM;
      } else if (item instanceof TripModel) {
         feedItem = new TripFeedItem();
         type = Type.TRIP;
      } else {
         feedItem = new UndefinedFeedItem();
         type = Type.UNDEFINED;
      }

      item.setComments(new ArrayList<>());
      feedItem.action = Action.ADD;
      feedItem.type = type;
      feedItem.item = item;
      feedItem.createdAt = Calendar.getInstance().getTime();
      feedItem.links = Links.forUser(owner);
      return feedItem;
   }

   public FeedItem(int id) {
      super();
      this.id = id;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Getters / Setters
   ///////////////////////////////////////////////////////////////////////////

   public int getNotificationId() {
      return notificationId;
   }

   @Override
   public Type getType() {
      return type;
   }

   public void setType(Type type) {
      this.type = type;
   }

   public Action getAction() {
      return action;
   }

   public void setAction(Action action) {
      this.action = action;
   }

   @Override
   public T getItem() {
      return item;
   }

   public void setItem(T item) {
      this.item = item;
   }

   public Links getLinks() {
      return links;
   }

   public void setLinks(Links links) {
      this.links = links;
   }

   public Date getCreatedAt() {
      return createdAt;
   }

   public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
   }

   public Date getReadAt() {
      return readAt;
   }

   public MetaData getMetaData() {
      return metaData;
   }

   public void setMetaData(MetaData metaData) {
      this.metaData = metaData;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Equals / Hashcode
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      FeedItem<?> that = (FeedItem<?>) o;

      if (item == null && super.equals(that)) return true;
      return !(item != null ? !item.equals(that.item) : that.item != null);
   }

   @Override
   public int hashCode() {
      if (item == null) return super.hashCode();
      return item != null ? item.hashCode() : 0;
   }

   public boolean equalsWith(@Nullable FeedItem feedItem) {
      if (feedItem == null) return false;
      return getItem().getUid().equals(feedItem.getItem().getUid()) && getAction().equals(feedItem.getAction());
   }

   public FeedItem() {
      super();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   protected FeedItem(Parcel in) {
      super(in);
      this.notificationId = in.readInt();
      int tmpType = in.readInt();
      this.type = tmpType == -1 ? null : Type.values()[tmpType];
      int tmpAction = in.readInt();
      this.action = tmpAction == -1 ? null : Action.values()[tmpAction];
      this.item = (T) in.readSerializable();
      this.links = in.readParcelable(Links.class.getClassLoader());
      long tmpCreatedAt = in.readLong();
      this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
      long tmpReadAt = in.readLong();
      this.readAt = tmpReadAt == -1 ? null : new Date(tmpReadAt);
      this.metaData = in.readParcelable(MetaData.class.getClassLoader());
   }

   @Override
   public int describeContents() {
      return 0;
   }


   @Override
   public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeInt(this.notificationId);
      dest.writeInt(this.type == null ? -1 : this.type.ordinal());
      dest.writeInt(this.action == null ? -1 : this.action.ordinal());
      dest.writeSerializable(this.item);
      dest.writeParcelable(this.links, flags);
      dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
      dest.writeLong(this.readAt != null ? this.readAt.getTime() : -1);
      dest.writeParcelable(this.metaData, flags);
   }


   public static final Creator<FeedItem> CREATOR = new Creator<FeedItem>() {
      @Override
      public FeedItem createFromParcel(Parcel in) {
         return new FeedItem(in);
      }

      @Override
      public FeedItem[] newArray(int size) {
         return new FeedItem[size];
      }
   };

   ///////////////////////////////////////////////////////////////////////////
   // Helpers
   ///////////////////////////////////////////////////////////////////////////

   public String detailsText(Resources resources) {
      String type = getTypeCaption(resources);
      String action = resources.getString(R.string.added);

      User user = getItem().getOwner();
      if (user == null) {
         return "";
      }
      String companyName = TextUtils.isEmpty(user.getCompany()) ? "" : " - " + user.getCompany();
      String result = resources.getString(R.string.feed_header, user.getFullName(), companyName, action, type);
      return result;
   }


   public String infoText(Resources resources, int accountId) {
      if (!links.hasUsers()) return null;

      String result;
      User actionOwner = links.getUsers().get(0);
      boolean isAccountsItem = item == null || item.getOwner() == null || accountId == item.getOwner().getId();
      boolean ownAction = isAccountsItem && accountId == actionOwner.getId();
      boolean isTrip = item instanceof TripModel;
      String action = getActionCaption(resources, isAccountsItem, ownAction, isTrip);
      String type = getTypeCaption(resources);
      String companyName = TextUtils.isEmpty(actionOwner.getCompany()) ? "" : " - " + actionOwner.getCompany();

      int usersCount = links.getUsers().size();
      if (usersCount == 1) {
         result = resources.getString(R.string.feed_header, actionOwner.getFullName(), companyName, action, type);
      } else if (usersCount == 2) {
         User user2 = links.getUsers().get(1);
         String companyName2 = TextUtils.isEmpty(user2.getCompany()) ? "" : " - " + user2.getCompany();
         result = resources.getString(R.string.feed_header_two, actionOwner.getFullName(), companyName, user2.getFullName(), companyName2, action, type);
      } else {
         result = resources.getString(R.string.feed_header_many, actionOwner.getFullName(), companyName, "" + (usersCount - 1), action, type);
      }
      return result;
   }

   private String getActionCaption(Resources resources, boolean isAccountsItem, boolean ownAction, boolean isTrip) {
      if (action == null) return "";
      //
      switch (action) {
         case BOOK:
            return resources.getString(R.string.booked);
         case UPDATE:
            return resources.getString(R.string.updated);
         case ADD:
            return resources.getString(R.string.added);
         case SHARE:
            return resources.getString(R.string.shared);
         case LIKE:
            return isAccountsItem && !ownAction && !isTrip ? resources.getString(R.string.liked) : resources.getString(R.string.liked_foreign);
         case ACCEPT_REQUEST:
            return resources.getString(R.string.accept_request);
         case REJECT_REQUEST:
            return resources.getString(R.string.reject_request);
         case COMMENT:
            return isAccountsItem && !ownAction ? resources.getString(R.string.comment) : resources.getString(R.string.comment_foreign);
         case SEND_REQUEST:
            return resources.getString(R.string.send_request);
         case TAG_PHOTO:
            return resources.getString(R.string.tag_photo);
      }
      return "";
   }


   private String getTypeCaption(Resources resources) {
      if (type == null) return "";

      switch (type) {
         case TRIP:
            return resources.getString(R.string.feed_trip);
         case PHOTO:
            return resources.getString(R.string.feed_photo);
         case BUCKET_LIST_ITEM:
            return resources.getString(R.string.feed_bucket);
         case POST:
            return "Post";
         case UNDEFINED:
         default:
            return "";
      }
   }

   public String previewImage(Resources resources) {
      return "";
   }

   ///////////////////////////////////////////////////////////////////////////
   // Translation
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public String getOriginalText() {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getTranslation() {
      return translation;
   }

   @Override
   public void setTranslation(String translation) {
      this.translation = translation;
   }

   @Override
   public boolean isTranslated() {
      return translated;
   }

   @Override
   public void setTranslated(boolean translated) {
      this.translated = translated;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Inner
   ///////////////////////////////////////////////////////////////////////////

   public enum Action {
      @SerializedName("share")
      SHARE,
      BOOK,
      @SerializedName("add")
      ADD,
      UPDATE,
      @SerializedName("like")
      LIKE,
      @SerializedName("comment")
      COMMENT,
      @SerializedName("accept_request")
      ACCEPT_REQUEST,
      @SerializedName("reject_request")
      REJECT_REQUEST,
      @SerializedName("send_request")
      SEND_REQUEST,
      @SerializedName("tag_photo")
      TAG_PHOTO,
      UNKNOWN
   }

}
