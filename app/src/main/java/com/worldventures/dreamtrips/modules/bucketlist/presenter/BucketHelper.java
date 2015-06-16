package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;

import com.gc.materialdesign.widgets.SnackBar;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

public class BucketHelper {

    public void notifyItemAddedToBucket(Activity activity, BucketItem item) {
        new SnackBar(activity, activity.getString(R.string.bucket_added, item.getName())).show();
    }

}
