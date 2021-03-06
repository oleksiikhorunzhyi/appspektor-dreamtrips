package com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.delegate;

import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.PopularBucketItem;

public interface BucketPopularCellDelegate extends CellDelegate<PopularBucketItem> {

   void addClicked(PopularBucketItem popularBucketItem, int position);

   void doneClicked(PopularBucketItem popularBucketItem, int position);
}
