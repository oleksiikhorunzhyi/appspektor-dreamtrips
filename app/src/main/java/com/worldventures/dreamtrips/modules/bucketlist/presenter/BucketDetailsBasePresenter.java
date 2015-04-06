package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BucketDetailsBasePresenter<VT extends BucketDetailsBasePresenter.View> extends Presenter<VT> {

    @Inject
    protected SnappyRepository db;

    protected BucketTabsFragment.Type type;
    protected BucketItem bucketItem;

    protected List<BucketItem> items = new ArrayList<>();

    protected RequestListener<BucketItem> requestListener = new RequestListener<BucketItem>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            view.informUser(R.string.bucket_item_edit_error);
        }

        @Override
        public void onRequestSuccess(BucketItem bucketItemUpdated) {
            onSuccess(bucketItemUpdated);
        }
    };

    public BucketDetailsBasePresenter(VT view, Bundle bundle) {
        super(view);
        type = (BucketTabsFragment.Type)
                bundle.getSerializable(BucketActivity.EXTRA_TYPE);
        bucketItem = (BucketItem)
                bundle.getSerializable(BucketActivity.EXTRA_ITEM);
    }

    @Override
    public void resume() {
        super.resume();
        items.addAll(db.readBucketList(type.name()));
        syncUI();
    }

    public void onEvent(BucketItemUpdatedEvent event) {
        bucketItem = event.getBucketItem();
    }

    protected void syncUI() {
        view.setTitle(bucketItem.getName());
        view.setDescription(bucketItem.getDescription());
        view.setStatus(bucketItem.isDone());
        view.setPeople(bucketItem.getFriends());
        view.setTags(bucketItem.getBucketTags());
        view.setTime(DateTimeUtils.convertDateToString(bucketItem.getTarget_date(), DateTimeUtils.DATE_FORMAT));
    }

    private void onSuccess(BucketItem bucketItemUpdated) {
        view.informUser(R.string.bucket_item_edit_done);
        int i = items.indexOf(bucketItemUpdated);
        items.remove(items.indexOf(bucketItemUpdated));
        items.add(i, bucketItemUpdated);
        db.saveBucketList(items, type.name());
        eventBus.post(new BucketItemUpdatedEvent(bucketItemUpdated));
        view.done();
    }

    public interface View extends Presenter.View {
        void setTitle(String title);

        void setDescription(String description);

        void setTime(String time);

        void setPeople(String people);

        void setTags(String tags);

        void setStatus(boolean isCompleted);

        void done();
    }
}
