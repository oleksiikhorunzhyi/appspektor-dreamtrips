package com.worldventures.dreamtrips.modules.bucketlist.presenter;


import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;

public class BucketListEditActivityPM extends ActivityPresenter {

    public BucketListEditActivityPM(View view) {
        super(view);
    }

    public void onCreate(Bundle bundle, Route route) {
        fragmentCompass.add(route, bundle);
    }

}
