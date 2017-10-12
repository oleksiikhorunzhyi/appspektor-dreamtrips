package com.worldventures.dreamtrips.wallet.ui.settings.help.video.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.core.modules.video.model.Video;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.holder.VideoTypeFactory;

public class WalletVideoModel extends BaseViewModel<VideoTypeFactory> implements Parcelable {

   private final Video video;

   public WalletVideoModel(Video video) {
      this.video = video;
      this.modelId = video.getVideoName();
   }

   public Video getVideo() {
      return video;
   }

   @Override
   public int describeContents() { return 0; }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(video.getImageUrl());
      dest.writeString(video.getVideoUrl());
      dest.writeString(video.getVideoName());
      dest.writeString(video.getCategory());
      dest.writeString(video.getDuration());
      dest.writeString(video.getLanguage());
   }

   protected WalletVideoModel(Parcel in) {
      final String imageUrl = in.readString();
      final String videoUrl = in.readString();
      final String name = in.readString();
      final String category = in.readString();
      final String duration = in.readString();
      final String language = in.readString();
      this.video = new Video(imageUrl, videoUrl, name, category, duration, language);
      this.modelId = name;
   }

   public static final Creator<WalletVideoModel> CREATOR = new Creator<WalletVideoModel>() {
      @Override
      public WalletVideoModel createFromParcel(Parcel source) {
         return new WalletVideoModel(source);
      }

      @Override
      public WalletVideoModel[] newArray(int size) {
         return new WalletVideoModel[size];
      }
   };

   @Override
   public int type(VideoTypeFactory typeFactory) {
      return typeFactory.type(this);
   }
}
