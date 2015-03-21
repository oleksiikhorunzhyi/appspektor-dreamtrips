package com.worldventures.dreamtrips.modules.bucketlist.presenter;


import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.BaseActivityPresenter;

/**
 * Created by 1 on 26.02.15.
 */
public class BucketListEditActivityPM extends BaseActivityPresenter {

    public BucketListEditActivityPM(View view) {
        super(view);
    }

    public void onCreate(Bundle bundle, Route route) {
        fragmentCompass.add(route, bundle);
    }

}
