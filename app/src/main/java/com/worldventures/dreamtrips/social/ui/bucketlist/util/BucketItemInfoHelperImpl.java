package com.worldventures.dreamtrips.social.ui.bucketlist.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.utils.TimeUtils;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;

public class BucketItemInfoHelperImpl implements BucketItemInfoHelper {

   private final Context context;

   public BucketItemInfoHelperImpl(Context context) {
      this.context = context;
   }

   @Override
   public String getMediumResUrl(BucketItem bucketItem) {
      int width = context.getResources().getDimensionPixelSize(R.dimen.bucket_popular_photo_width);
      return bucketItem.getCoverUrl(width, width);
   }

   @Override
   public String getHighResUrl(BucketItem bucketItem) {
      int width = context.getResources().getDimensionPixelSize(R.dimen.bucket_popular_cover_width);
      return bucketItem.getCoverUrl(width, width);
   }

   @Override
   public @Nullable String getPlace(BucketItem bucketItem) {
      String place = null;
      if (bucketItem.getLocation() != null) {
         place = bucketItem.getLocation().getName();
      }
      if (bucketItem.getDining() != null && !TextUtils.isEmpty(bucketItem.getDining()
            .getCity()) && !TextUtils.isEmpty(bucketItem.getDining().getCountry())) {
         place = TextUtils.join(", ", new String[]{bucketItem.getDining().getCity(), bucketItem.getDining().getCountry()});
      }
      return place;
   }

   @Override
   public String getTime(BucketItem bucketItem) {
      String time = TimeUtils.convertDateToReference(context, bucketItem.getTargetDate());
      if (TextUtils.isEmpty(time)) {
         return context.getString(R.string.someday);
      } else {
         return time;
      }
   }
}
