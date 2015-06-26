package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;

import java.util.List;

public class BucketHelper {

    public void notifyItemAddedToBucket(Activity activity, BucketItem item) {
        Snackbar.make(null, activity.getString(R.string.bucket_added, item.getName()), Snackbar.LENGTH_SHORT).show();
    }

}
