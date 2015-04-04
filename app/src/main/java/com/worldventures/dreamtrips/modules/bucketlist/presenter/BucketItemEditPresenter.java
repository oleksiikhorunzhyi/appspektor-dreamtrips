package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.bucketlist.api.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenter.View> {

    public BucketItemEditPresenter(View view, Bundle bundle) {
        super(view, bundle);
    }

    @Override
    public void resume() {
        super.resume();
        List<CategoryItem> list = db.readList(SnappyRepository.CATEGORIES, CategoryItem.class);
        if (!list.isEmpty()) {
            view.setCategoryItems(list);
            view.setCategory(list.indexOf(bucketItem.getCategory()));
        }

        if(!bucketItem.getImages().isEmpty()){
            view.addImages(bucketItem.getImages());
        }
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
        UpdateBucketItemCommand updateBucketItemCommand =
                new UpdateBucketItemCommand(bucketItem.getId(), bucketPostItem);
        dreamSpiceManager.execute(updateBucketItemCommand, requestListener);
    }

    public Date getDate() {
        if (bucketItem.getTarget_date() != null) {
            return bucketItem.getTarget_date();
        } else {
            return Calendar.getInstance().getTime();
        }
    }

    public void onDataSet(int year, int month, int day) {
        view.setTime(DateTimeUtils.convertDateToString(year, month, day));
    }

    public List<String> getListFromString(String temp) {
        if (TextUtils.isEmpty(temp)) {
            return Collections.emptyList();
        } else {
            return Queryable.from(temp.split(",")).map(String::trim).toList();
        }
    }

    public interface View extends BucketDetailsBasePresenter.View {
        void setCategory(int selection);

        void setCategoryItems(List<CategoryItem> items);

        CategoryItem getSelectedItem();

        boolean getStatus();

        String getTags();

        String getPeople();

        String getTime();

        String getTitle();

        String getDescription();

        void addImages(List<BucketPhoto> images);

    }


}
