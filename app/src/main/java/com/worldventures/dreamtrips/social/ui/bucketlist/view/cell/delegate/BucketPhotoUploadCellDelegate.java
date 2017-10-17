package com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.delegate;

import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;

public interface BucketPhotoUploadCellDelegate extends CellDelegate<EntityStateHolder<BucketPhoto>> {
   void deletePhotoRequest(BucketPhoto photoModel);

   void selectPhotoAsCover(BucketPhoto model);
}