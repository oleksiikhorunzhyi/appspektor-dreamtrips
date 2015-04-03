package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.BucketItemAddedEvent;
import com.worldventures.dreamtrips.core.utils.events.BucketItemReloadEvent;
import com.worldventures.dreamtrips.modules.bucketlist.api.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class BucketItemEditPresenter extends Presenter<BucketItemEditPresenter.View> {

    @Inject
    protected SnappyRepository db;

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
        view.setPeople(bucketItem.getFriends());
        view.setTags(bucketItem.getBucketTags());
        view.setTime(DateTimeUtils.convertDateToString(bucketItem.getTarget_date(), DateTimeUtils.DATE_FORMAT));

        List<CategoryItem> list = db.readList(SnappyRepository.CATEGORIES, CategoryItem.class);
        if (!list.isEmpty()) {
            view.setCategoryItems(list);
            view.setCategory(list.indexOf(bucketItem.getCategory()));
        }

        items.addAll(db.readBucketList(type.name()));
    }

    public void saveItem() {
        BucketPostItem bucketPostItem = new BucketPostItem();
        bucketPostItem.setName(view.getTitle());
        bucketPostItem.setDescription(view.getDescription());
        bucketPostItem.setStatus(view.getStatus());
        bucketPostItem.setTags(getListFromString(view.getTags()));
        bucketPostItem.setPeople(getListFromString(view.getPeople()));
        bucketPostItem.setCategory(view.getSelectedItem());
        Date date = DateTimeUtils.dateFromString(view.getTime(), DateTimeUtils.DATE_FORMAT);
        bucketPostItem.setDate(date);
        UpdateBucketItemCommand updateBucketItemCommand = new UpdateBucketItemCommand(bucketItem.getId(), bucketPostItem);
        dreamSpiceManager.execute(updateBucketItemCommand, updateListener);
    }

    private RequestListener<BucketItem> updateListener = new RequestListener<BucketItem>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            view.informUser(R.string.bucket_item_edit_error);
        }

        @Override
        public void onRequestSuccess(BucketItem bucketItem) {
            view.informUser(R.string.bucket_item_edit_done);
            int i = items.indexOf(bucketItem);
            items.remove(items.indexOf(bucketItem));
            items.add(i, bucketItem);
            db.saveBucketList(items, type.name());
            eventBus.post(new BucketItemReloadEvent());
            view.done();
        }
    };

    public void onDataSet(int year, int month, int day) {
        view.setTime(DateTimeUtils.convertDateToString(year, month, day));
    }

    public Date getDate() {
        Date date = bucketItem.getTarget_date();
        return date != null ? date : Calendar.getInstance().getTime();
    }

    public List<String> getListFromString(String temp) {
        if (TextUtils.isEmpty(temp)) {
            return Collections.emptyList();
        } else {
            return Queryable.from(temp.split(",")).map((s) -> s.trim()).toList();
        }
    }

    public interface View extends Presenter.View {
        void setTitle(String title);

        void setDescription(String description);

        void setTime(String time);

        void setPeople(String people);

        void setTags(String tags);

        void setStatus(boolean isCompleted);

        void setCategory(int selection);

        void setCategoryItems(List<CategoryItem> items);

        void done();

        CategoryItem getSelectedItem();

        boolean getStatus();

        String getTags();

        String getPeople();

        String getTime();

        String getTitle();

        String getDescription();
    }


}
