package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;

@Layout(R.layout.layout_detailed_bucket_item)
public class BucketDetailsFragment extends BaseFragment<BucketItemDetailsPresenter> implements BucketItemDetailsPresenter.View {

    @InjectView(R.id.imageViewCover)
    protected ImageView imageViewCover;

    @InjectView(R.id.textViewName)
    protected TextView textViewName;

    @InjectView(R.id.textViewFriends)
    protected TextView textViewFriends;

    @InjectView(R.id.textViewTags)
    protected TextView textViewTags;

    @InjectView(R.id.textViewDescription)
    protected TextView textViewDescription;

    @InjectView(R.id.textViewCategory)
    protected TextView textViewCategory;

    @InjectView(R.id.textViewDate)
    protected TextView textViewDate;

    @InjectView(R.id.textViewPlace)
    protected TextView textViewPlace;

    @InjectView(R.id.recyclerViewBucketPhotos)
    protected RecyclerView recyclerViewBucketPhotos;

    @InjectView(R.id.checkBoxDone)
    protected CheckBox checkBox;

    @Override
    protected BucketItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketItemDetailsPresenter(this, getArguments());
    }

    @Override
    public void setTitle(String title) {
        textViewName.setText(title);
    }

    @Override
    public void setDescription(String description) {
        textViewDescription.setText(description);
    }

    @Override
    public void setTime(String time) {
        textViewDate.setText(time);
    }

    @Override
    public void setPeople(String people) {
        textViewFriends.setText(people);
    }

    @Override
    public void setTags(String tags) {
        textViewTags.setText(tags);
    }

    @Override
    public void setStatus(boolean completed) {
        checkBox.setChecked(completed);
    }

    @Override
    public void setCategory(String category) {
        textViewCategory.setText(category);
    }

    @Override
    public void done() {

    }
}
