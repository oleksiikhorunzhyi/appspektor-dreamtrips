package com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

public interface BucketPhotoCellDelegate extends CellDelegate<BucketPhoto> {

    void deletePhotoRequest(BucketPhoto photo);

}
