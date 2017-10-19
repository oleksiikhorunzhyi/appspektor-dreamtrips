package com.worldventures.dreamtrips.social.ui.bucketlist.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.core.ui.view.custom.horizontal_photo_view.StatefulHorizontalPhotosView;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;

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