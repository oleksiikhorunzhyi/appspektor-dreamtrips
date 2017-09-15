package com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;

public interface BucketPhotoUploadCellDelegate extends CellDelegate<EntityStateHolder<BucketPhoto>> {
   void deletePhotoRequest(BucketPhoto photoModel);

   void selectPhotoAsCover(BucketPhoto model);
}