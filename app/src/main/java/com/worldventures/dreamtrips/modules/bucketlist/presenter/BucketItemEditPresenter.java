package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenterView> {

    private Date selectedDate;

    private boolean savingItem = false;

    public BucketItemEditPresenter(Bundle bundle) {
        super(bundle);
        selectedDate = bucketItem.getTarget_date();
    }

    @Override
    public void takeView(BucketItemEditPresenterView view) {
        priorityEventBus = 1;
        super.takeView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<CategoryItem> list = db.readList(SnappyRepository.CATEGORIES, CategoryItem.class);
        if (!list.isEmpty()) {
            view.setCategoryItems(list);
            view.setCategory(list.indexOf(bucketItem.getCategory()));
        }
    }

    @Override
    protected void syncUI() {
        super.syncUI();
        view.updatePhotos();
    }

    public void saveItem() {
        savingItem = true;
        BucketPostItem bucketPostItem = new BucketPostItem();
        bucketPostItem.setName(view.getTitle());
        bucketPostItem.setDescription(view.getDescription());
        bucketPostItem.setStatus(view.getStatus());
        bucketPostItem.setTags(getListFromString(view.getTags()));
        bucketPostItem.setPeople(getListFromString(view.getPeople()));
        bucketPostItem.setCategory(view.getSelectedItem());
        bucketPostItem.setDate(selectedDate);
        bucketItemManager.updateBucketItem(bucketPostItem, type, item -> {
            if (savingItem) {
                savingItem = false;
                view.done();
            }
        }, this);
    }

    public Date getDate() {
        if (bucketItem.getTarget_date() != null) {
            return bucketItem.getTarget_date();
        } else {
            return Calendar.getInstance().getTime();
        }
    }

    public void onDateSet(int year, int month, int day) {
        String date = DateTimeUtils.convertDateToString(year, month, day);
        view.setTime(date);
        setDate(DateTimeUtils.dateFromString(date));
    }

    public void setDate(Date date) {
        this.selectedDate = date;
    }

    public void onDateClear() {
        view.setTime(context.getString(R.string.someday));
        setDate(null);
    }

    public List<String> getListFromString(String temp) {
        if (TextUtils.isEmpty(temp)) {
            return Collections.emptyList();
        } else {
            return Queryable.from(temp.split(",")).map(String::trim).toList();
        }
    }
}
