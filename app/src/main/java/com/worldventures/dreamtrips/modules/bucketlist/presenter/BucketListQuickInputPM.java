package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.BucketItemAddedEvent;
import com.worldventures.dreamtrips.core.utils.events.BucketItemReloadEvent;
import com.worldventures.dreamtrips.modules.bucketlist.api.AddBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by 1 on 26.02.15.
 */
public class BucketListQuickInputPM extends Presenter<BucketListQuickInputPM.View> {

    @Inject
    SnappyRepository db;
    @Global
    @Inject
    EventBus eventBus;

    private BucketTabsFragment.Type type;

    public BucketListQuickInputPM(View view, BucketTabsFragment.Type type) {
        super(view);
        this.type = type;
    }


    @Override
    public void init() {
        super.init();
        eventBus.register(this);
    }

    public void frameClicked() {
        fragmentCompass.pop();
    }

    public interface View extends Presenter.View {
    }
}
