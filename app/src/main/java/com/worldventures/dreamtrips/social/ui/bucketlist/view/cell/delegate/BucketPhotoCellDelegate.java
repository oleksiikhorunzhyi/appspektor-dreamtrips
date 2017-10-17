package com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.delegate;

import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;

public interface BucketPhotoCellDelegate extends CellDelegate<BucketPhoto> {

   void deletePhotoRequest(BucketPhoto photo);

   void choosePhoto(BucketPhoto photo);
}
