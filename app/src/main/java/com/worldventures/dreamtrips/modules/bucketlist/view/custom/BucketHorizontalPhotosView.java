package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;
import com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.StatefulHorizontalPhotosView;

public class BucketHorizontalPhotosView extends StatefulHorizontalPhotosView<BucketPhoto, BucketPhotoUploadCellDelegate> {

   public BucketHorizontalPhotosView(Context context) {
      super(context);
   }

   public BucketHorizontalPhotosView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected Class<?> getPhotocellClass() {
      return BucketPhotoCell.class;
   }
}