package com.worldventures.dreamtrips.modules.bucketlist.presenter;


import android.os.Bundle;

import com.worldventures.dreamtrips.core.navigation.State;
import com.worldventures.dreamtrips.modules.common.presenter.BaseActivityPresentation;

/**
 * Created by 1 on 26.02.15.
 */
public class BucketListEditActivityPM extends BaseActivityPresentation {

    public BucketListEditActivityPM(View view) {
        super(view);
    }

    public void onCreate(Bundle bundle, State state) {
        fragmentCompass.add(state, bundle);
    }

}
