package com.worldventures.dreamtrips.modules.facebook.model;

import android.os.Bundle;

import com.facebook.model.GraphObject;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.facebook.FacebookUtils;

import java.io.Serializable;


public class FacebookAlbum implements Serializable {

   private static final String ID = "id";
   private static final String FROM = "from";
   private static final String NAME = "name";
   private static final String DESCRIPTION = "description";
   private static final String MESSAGE = "message";
   private static final String LOCATION = "location";
   private static final String LINK = "link";
   private static final String COUNT = "count";
   private static final String PRIVACY = "privacy";
   private static final String COVER_PHOTO = "cover_photo";
   private static final String TYPE = "type";
   private static final String CREATED_TIME = "created_time";
   private static final String UPDATED_TIME = "updated_time";
   private static final String CAN_UPLOAD = "can_upload";

   private final GraphObject mGraphObject;
   private String mId = null;
   private User mFrom = null;
   private String mName = null;
   private String mDescription = null;
   private String mLocation = null;
   private String mLink = null;
   private Integer mCount = null;
   private String mPrivacy = null;
   private String mCoverPhotoId = null;
   private String mType = null;
   private Long mCreatedTime;
   private Long mUpdatedTime;
   private boolean mCanUpload;

   private FacebookAlbum(GraphObject graphObject) {
      mGraphObject = graphObject;
      if (graphObject == null) {
         return;
      }

      mId = FacebookUtils.getPropertyString(graphObject, ID);
      mName = FacebookUtils.getPropertyString(graphObject, NAME);
      mDescription = FacebookUtils.getPropertyString(graphObject, DESCRIPTION);
      mLocation = FacebookUtils.getPropertyString(graphObject, LOCATION);
      mLink = FacebookUtils.getPropertyString(graphObject, LINK);
      mCount = FacebookUtils.getPropertyInteger(graphObject, COUNT);
      mPrivacy = FacebookUtils.getPropertyString(graphObject, PRIVACY);
      mCoverPhotoId = FacebookUtils.getPropertyString(graphObject, COVER_PHOTO);
      mType = FacebookUtils.getPropertyString(graphObject, TYPE);
      mCreatedTime = FacebookUtils.getPropertyLong(graphObject, CREATED_TIME);
      mUpdatedTime = FacebookUtils.getPropertyLong(graphObject, UPDATED_TIME);
      mCanUpload = FacebookUtils.getPropertyBoolean(graphObject, CAN_UPLOAD);
   }

   private FacebookAlbum(Builder builder) {
      mGraphObject = null;
      mName = builder.mName;
      mDescription = builder.mMessage;
   }

   public static FacebookAlbum create(GraphObject graphObject) {
      return new FacebookAlbum(graphObject);
   }

   public GraphObject getGraphObject() {
      return mGraphObject;
   }

   /**
    * The album id.
    *
    * @return The album id
    */
   public String getId() {
      return mId;
   }

   /**
    * The user who created this album.
    *
    * @return The user who created this album
    */
   public User getFrom() {
      return mFrom;
   }

   /**
    * The title of the album.
    *
    * @return The title of the album
    */
   public String getName() {
      return mName;
   }

   /**
    * The description of the album.
    *
    * @return The description of the album
    */
   public String getDescription() {
      return mDescription;
   }

   /**
    * The location of the album.
    *
    * @return The location of the album
    */
   public String getLocation() {
      return mLocation;
   }

   /**
    * A link to this album on Facebook.
    *
    * @return A link to this album on Facebook
    */
   public String getLink() {
      return mLink;
   }

   /**
    * The number of photos in this album.
    *
    * @return The number of photos in this album
    */
   public Integer getCount() {
      return mCount;
   }

   /**
    * The privacy settings for the album.
    *
    * @return The privacy settings for the album
    */
   public String getPrivacy() {
      return mPrivacy;
   }

   /**
    * The album cover photo id.
    *
    * @return The album cover photo id
    */
   public String getCoverPhotoId() {
      return mCoverPhotoId;
   }

   public String getCoverUrl(String token) {
      return "https://graph.facebook.com/" + getCoverPhotoId() + "/picture?type=album&access_token=" + token + "&type=normal";
   }

   /**
    * The type of the album.
    *
    * @return The type of the album
    */
   public String getType() {
      return mType;
   }

   /**
    * The time the photo album was initially created.
    *
    * @return The time the photo album was initially created
    */
   public long getCreatedTime() {
      return mCreatedTime;
   }

   /**
    * The last time the photo album was updated.
    *
    * @return The last time the photo album was updated
    */
   public long getUpdatedTime() {
      return mUpdatedTime;
   }

   /**
    * Determines whether the user can upload to the album and returns true if
    * the user owns the album, the album is not full, and the app can add
    * photos to the album. <br>
    * <br>
    * <b>Important</b> The privacy setting of the app should be at minimum as
    * the privacy setting of the album ({@link #getPrivacy()}.
    *
    * @return <code>True</code> if user can upload to this album
    */
   public boolean canUpload() {
      return mCanUpload;
   }

   public Bundle getBundle() {
      Bundle bundle = new Bundle();

      // add name
      if (mName != null) {
         bundle.putString(NAME, mName);
      }

      // add description
      if (mDescription != null) {
         bundle.putString(MESSAGE, mDescription);
      }
      return bundle;
   }

   /**
    * Builder for preparing the Album object to be published.
    */
   public static class Builder {
      private String mName = null;
      private String mMessage = null;

      public Builder() {
      }

      public Builder setName(String name) {
         mName = name;
         return this;
      }

      public Builder setMessage(String message) {
         mMessage = message;
         return this;
      }

      public FacebookAlbum build() {
         return new FacebookAlbum(this);
      }
   }
}
