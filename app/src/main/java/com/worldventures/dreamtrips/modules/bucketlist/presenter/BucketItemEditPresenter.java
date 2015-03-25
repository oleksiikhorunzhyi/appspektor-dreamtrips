package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

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
        view.setTime(bucketItem.getCompletion_date());

    }

    public void saveItem() {

    }

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

        void setLocation(String location);

        void setTime(Date time);

        void setPeople(String people);

        void setTags(String tags);

        String getTags();

        String getPeople();

        Date getTime();

        String getTitle();

        String getDescription();
    }


}
