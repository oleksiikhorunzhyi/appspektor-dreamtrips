package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 *  1 on 26.02.15.
 */
public class BucketItemEditPresenter extends Presenter<BucketItemEditPresenter.View> {

    @Inject
    SnappyRepository db;
    @Global
    @Inject
    EventBus eventBus;

    private BucketTabsFragment.Type type;

    public BucketItemEditPresenter(View view, BucketTabsFragment.Type type) {
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
