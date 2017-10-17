package com.worldventures.dreamtrips.social.ui.feed.model;

import android.content.res.Resources;
import android.os.Parcel;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.core.utils.ImageUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class PhotoFeedItem extends FeedItem<Photo> {

   public PhotoFeedItem() {
   }

   public PhotoFeedItem(Parcel in) {
      super(in);
   }

   @Override
   public String previewImage(Resources res) {
      int width = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_w);
      int height = res.getDimensionPixelSize(R.dimen.bucket_cover_thumb_h);
      if (getItem().getImagePath() != null) {
         return ImageUtils.getParametrizedUrl(getItem().getImagePath(), width, height);
      } else return null;
   }

   public static final Creator<PhotoFeedItem> CREATOR = new Creator<PhotoFeedItem>() {
      @Override
      public PhotoFeedItem createFromParcel(Parcel in) {
         return new PhotoFeedItem(in);
      }

      @Override
      public PhotoFeedItem[] newArray(int size) {
         return new PhotoFeedItem[size];
      }
   };
}
