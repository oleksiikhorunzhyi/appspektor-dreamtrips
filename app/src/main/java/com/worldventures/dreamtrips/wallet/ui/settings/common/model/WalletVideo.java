package com.worldventures.dreamtrips.wallet.ui.settings.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.video.model.CachedModel;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.VideoTypeFactory;

public class WalletVideo extends BaseViewModel<VideoTypeFactory> implements Parcelable {

   private static final String FEATURED = "Featured";

   private String imageUrl;
   private String videoUrl;
   private String name;
   private String category;
   private String duration;
   private String language;

   private transient CachedModel entity;

   public WalletVideo(String imageUrl, String videoUrl, String name, String category, String duration, String language) {
      this.imageUrl = imageUrl;
      this.videoUrl = videoUrl;
      this.name = name;
      this.category = category;
      this.duration = duration;
      this.language = language;
   }

   public WalletVideo() {}

   public void setCategory(String category) {
      this.category = category;
   }

   public String getCategory() {
      return category;
   }

   public String getImageUrl() {
      return imageUrl;
   }

   public String getVideoUrl() {
      return videoUrl;
   }

   public String getUid() {
      return videoUrl;
   }

   public String getVideoName() {
      return name;
   }

   public String getDuration() {
      return duration;
   }

   public String getLanguage() {
      return language;
   }

   public CachedModel getCacheEntity() {
      if (entity == null) {
         entity = new CachedModel(getVideoUrl(), getUid(), getVideoName());
         entity.setEntityClass(Video.class);
      }
      return entity;
   }

   public void setCacheEntity(CachedModel entity) {
      this.entity = entity;
   }

   public boolean isFeatured() {
      return !TextUtils.isEmpty(category) && category.trim().equals(FEATURED);
   }

   public boolean isRecent() {
      return !isFeatured();
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.imageUrl);
      dest.writeString(this.videoUrl);
      dest.writeString(this.name);
      dest.writeString(this.category);
      dest.writeString(this.duration);
      dest.writeString(this.language);
   }

   protected WalletVideo(Parcel in) {
      this.imageUrl = in.readString();
      this.videoUrl = in.readString();
      this.name = in.readString();
      this.category = in.readString();
      this.duration = in.readString();
      this.language = in.readString();
   }

   public static final Creator<WalletVideo> CREATOR = new Creator<WalletVideo>() {
      @Override
      public WalletVideo createFromParcel(Parcel source) {return new WalletVideo(source);}

      @Override
      public WalletVideo[] newArray(int size) {return new WalletVideo[size];}
   };

   @Override
   public int type(VideoTypeFactory typeFactory) {
      return typeFactory.type(this);
   }
}
