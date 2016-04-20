package com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;

public interface BucketPopularCellDelegate extends CellDelegate<PopularBucketItem> {

    void addClicked(PopularBucketItem popularBucketItem, int position);

    void doneClicked(PopularBucketItem popularBucketItem, int position);
}
