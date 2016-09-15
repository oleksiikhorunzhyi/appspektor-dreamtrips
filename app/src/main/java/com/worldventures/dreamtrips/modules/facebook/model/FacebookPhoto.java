package com.worldventures.dreamtrips.modules.facebook.model;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

import com.facebook.model.GraphObject;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.facebook.FacebookUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

public class FacebookPhoto implements BasePhotoPickerModel, Serializable {

   private static final String ID = "id";
   private static final String ALBUM = "album";
   private static final String BACKDATED_TIME = "backdated_time";
   private static final String BACKDATED_TIME_GRANULARITY = "backdate_time_granularity";
   private static final String CREATED_TIME = "created_time";
   private static final String FROM = "from";
   private static final String HEIGHT = "height";
   private static final String ICON = "icon";
   private static final String IMAGES = "images";
   private static final String LINK = "link";
   private static final String PAGE_STORY_ID = "page_story_id";
   private static final String PICTURE = "picture";
   private static final String PLACE = "place";
   private static final String SOURCE = "source";
   private static final String UPDATED_TIME = "updated_time";
   private static final String WIDTH = "width";
   private static final String NAME = "name";
   private static final String MESSAGE = "message"; // same as NAME
   private static final String PRIVACY = "privacy";

   private String mId;
   private Long mBackDatetime;
   private BackDatetimeGranularity mBackDatetimeGranularity;
   private Long mCreatedTime;
   private Integer mHeight;
   private String mIcon;
   private List<ImageSource> mImageSources;
   private String mLink;
   private String mName;
   private String mPageStoryId;
   private String mPicture;
   private String mSource;
   private Long mUpdatedTime;
   private Integer mWidth;

   private String mPlaceId = null;
   private Parcelable mParcelable = null;
   private byte[] mBytes = null;

   private boolean checked;
   private long pickedTime;

   private FacebookPhoto(GraphObject graphObject) {

      if (graphObject == null) return;

      // id
      mId = FacebookUtils.getPropertyString(graphObject, ID);

      // back date time
      mBackDatetime = FacebookUtils.getPropertyLong(graphObject, BACKDATED_TIME);

      // back date time granularity
      String granularity = FacebookUtils.getPropertyString(graphObject, BACKDATED_TIME_GRANULARITY);
      mBackDatetimeGranularity = BackDatetimeGranularity.fromValue(granularity);

      // created time
      mCreatedTime = FacebookUtils.getPropertyLong(graphObject, CREATED_TIME);

      // height
      mHeight = FacebookUtils.getPropertyInteger(graphObject, HEIGHT);

      // icon
      mIcon = FacebookUtils.getPropertyString(graphObject, ICON);

      // image sources
      mImageSources = FacebookUtils.createList(graphObject, IMAGES, new FacebookUtils.Converter<ImageSource>() {
         @Override
         public ImageSource convert(GraphObject graphObject) {
            ImageSource imageSource = new ImageSource();
            imageSource.mHeight = FacebookUtils.getPropertyInteger(graphObject, HEIGHT);
            imageSource.mWidth = FacebookUtils.getPropertyInteger(graphObject, WIDTH);
            imageSource.mSource = FacebookUtils.getPropertyString(graphObject, SOURCE);
            return imageSource;
         }
      });
      mLink = FacebookUtils.getPropertyString(graphObject, LINK);
      mName = FacebookUtils.getPropertyString(graphObject, NAME);
      mPageStoryId = FacebookUtils.getPropertyString(graphObject, PAGE_STORY_ID);
      mPicture = FacebookUtils.getPropertyString(graphObject, PICTURE);
      mSource = FacebookUtils.getPropertyString(graphObject, SOURCE);
      mUpdatedTime = FacebookUtils.getPropertyLong(graphObject, UPDATED_TIME);
      mWidth = FacebookUtils.getPropertyInteger(graphObject, WIDTH);


   }

   private FacebookPhoto(Builder builder) {
      mName = builder.mName;
      mPlaceId = builder.mPlaceId;
      mParcelable = builder.mParcelable;
      mBytes = builder.mBytes;
   }

   public static FacebookPhoto create(GraphObject graphObject) {
      return new FacebookPhoto(graphObject);
   }

   /**
    * Get id of the photo
    *
    * @return
    */
   public String getId() {
      return mId;
   }

   public Long getBackDateTime() {
      return mBackDatetime;
   }

   public BackDatetimeGranularity getBackDatetimeGranularity() {
      return mBackDatetimeGranularity;
   }

   public Long getCreatedTime() {
      return mCreatedTime;
   }

   public Integer getHeight() {
      return mHeight;
   }

   public String getIcon() {
      return mIcon;
   }

   public List<ImageSource> getImageSources() {
      return mImageSources;
   }

   public String getLink() {
      return mLink;
   }

   public String getName() {
      return mName;
   }

   public String getPageStoryId() {
      return mPageStoryId;
   }

   public String getPicture() {
      return mPicture;
   }


   public String getSource() {
      return mSource;
   }

   public Long getUpdatedTime() {
      return mUpdatedTime;
   }

   public Integer getWidth() {
      return mWidth;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   @Override
   public String getThumbnailPath() {
      if (mImageSources.size() > 2) {
         return mImageSources.get(mImageSources.size() / 2 + 1).getSource();
      } else {
         return getSource();
      }
   }

   @Override
   public String getOriginalPath() {
      if (mImageSources.size() > 2) {
         return mImageSources.get(0).getSource();
      } else {
         return getSource();
      }
   }

   @Override
   public long getPickedTime() {
      return pickedTime;
   }

   public void setPickedTime(long pickedTime) {
      this.pickedTime = pickedTime;
   }

   public boolean isChecked() {
      return checked;
   }

   /**
    * Is used for publishing action
    */
   public Parcelable getParcelable() {
      return mParcelable;
   }

   /**
    * Is used for publishing action
    */
   public String getPlaceId() {
      return mPlaceId;
   }

   public Bundle getBundle() {
      Bundle bundle = new Bundle();

      // add description
      if (mName != null) {
         bundle.putString(MESSAGE, mName);
      }

      // add place
      if (mPlaceId != null) {
         bundle.putString(PLACE, mPlaceId);
      }
      // add image
      if (mParcelable != null) {
         bundle.putParcelable(PICTURE, mParcelable);
      } else if (mBytes != null) {
         bundle.putByteArray(PICTURE, mBytes);
      }

      return bundle;
   }

   public enum BackDatetimeGranularity {
      YEAR("year"),
      MONTH("month"),
      DAY("day"),
      HOUR("hour"),
      MIN("min"),
      NONE("none");

      private String mValue;

      private BackDatetimeGranularity(String value) {
         mValue = value;
      }

      public static BackDatetimeGranularity fromValue(String value) {
         for (BackDatetimeGranularity granularityEnum : values()) {
            if (granularityEnum.mValue.equals(value)) {
               return granularityEnum;
            }
         }
         return BackDatetimeGranularity.NONE;
      }

      public String getValue() {
         return mValue;
      }
   }

   public static class ImageSource implements Serializable {

      private Integer mHeight;
      private String mSource;
      private Integer mWidth;

      public Integer getHeight() {
         return mHeight;
      }

      public Integer getWidth() {
         return mWidth;
      }

      public String getSource() {
         return mSource;
      }
   }

   /**
    * Builder for preparing the Photo object to be published.
    */
   public static class Builder {
      private String mName = null;
      private String mPlaceId = null;

      private Parcelable mParcelable = null;
      private byte[] mBytes = null;

      public Builder() {
      }

      /**
       * Set photo to be published
       *
       * @param bitmap
       */
      public Builder setImage(Bitmap bitmap) {
         mParcelable = bitmap;
         return this;
      }

      /**
       * Set photo to be published
       *
       * @param file
       */
      public Builder setImage(File file) {
         try {
            mParcelable = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
         } catch (FileNotFoundException e) {
            Timber.e(e, "Problem on setting image");

         }
         return this;
      }


      public Builder setImage(byte[] bytes) {
         mBytes = bytes;
         return this;
      }

      public Builder setName(String name) {
         mName = name;
         return this;
      }

      public Builder setPlace(String placeId) {
         mPlaceId = placeId;
         return this;
      }

      public FacebookPhoto build() {
         return new FacebookPhoto(this);
      }
   }

}
