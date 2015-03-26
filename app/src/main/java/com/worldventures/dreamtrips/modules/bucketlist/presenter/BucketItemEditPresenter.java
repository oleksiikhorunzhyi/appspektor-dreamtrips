package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.api.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * 1 on 26.02.15.
 */
public class BucketItemEditPresenter extends Presenter<BucketItemEditPresenter.View> {

    @Inject
    SnappyRepository db;

    private BucketTabsFragment.Type type;
    private BucketItem bucketItem;

    private List<BucketItem> items = new ArrayList<>();

    public BucketItemEditPresenter(View view, BucketTabsFragment.Type type, BucketItem bucketItem) {
        super(view);
        this.type = type;
        this.bucketItem = bucketItem;
    }

    @Override
    public void resume() {
        super.resume();
        view.setTitle(bucketItem.getName());
        view.setDescription(bucketItem.getDescription());
        view.setStatus(bucketItem.isDone());
        view.setTags(bucketItem.getBucketTags());

        items.addAll(db.readBucketList(type.name()));
    }

    public void saveItem() {
        BucketPostItem bucketPostItem = new BucketPostItem();
        bucketPostItem.setName(view.getTitle());
        bucketPostItem.setDescription(view.getDescription());
        bucketPostItem.setStatus(view.getStatus());
        bucketPostItem.setTags(getListFromString(view.getTags()));
        bucketPostItem.setPeople(getListFromString(view.getPeople()));
        UpdateBucketItemCommand updateBucketItemCommand = new UpdateBucketItemCommand(bucketItem.getId(), bucketPostItem);
        dreamSpiceManager.execute(updateBucketItemCommand, updateListener);
    }

    private RequestListener<BucketItem> updateListener = new RequestListener<BucketItem>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            view.informUser(spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(BucketItem bucketItem) {
            view.informUser("Saved!");
            int i = items.indexOf(bucketItem);
            items.remove(items.indexOf(bucketItem));
            items.add(i, bucketItem);
            db.saveBucketList(items, type.name());
        }
    };

    public Date getDate() {
        Date date = bucketItem.getCompletion_date();
        return date != null ? date : Calendar.getInstance().getTime();
    }

    public List<String> getListFromString(String temp) {
        return Queryable.from(temp.split(",")).map((s) -> s.trim()).toList();
    }

    public void frameClicked() {
        fragmentCompass.pop();
    }

    public interface View extends Presenter.View {
        void setTitle(String title);

        void setDescription(String description);

        void setTime(String time);

        void setPeople(String people);

        void setTags(String tags);

        void setStatus(boolean isCompleted);

        boolean getStatus();

        String getTags();

        String getPeople();

        String getTime();

        String getTitle();

        String getDescription();
    }


}
