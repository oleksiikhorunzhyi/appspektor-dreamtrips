package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

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

    @InjectView(R.id.checkBoxDone)
    protected CheckBox checkBox;

    @Inject
    protected UniversalImageLoader universalImageLoader;

    @Override
    protected BucketItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketItemDetailsPresenter(this, getArguments());
    }

    @Override
    public void setCover(String imageUrl) {
        universalImageLoader.loadImage(imageUrl, imageViewCover,
                UniversalImageLoader.OP_FULL_SCREEN);
    }

    @OnClick(R.id.imageViewEdit)
    protected void onEdit() {
        getPresenter().onEdit();
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
        if (TextUtils.isEmpty(time)) {
            textViewDate.setText(R.string.someday);
        } else {
            textViewDate.setText(time);
        }
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
        getActivity().onBackPressed();
    }

    @Override
    public void showEditContainer() {
        getActivity().findViewById(R.id.container_edit).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isTabletLandscape() {
        return ViewUtils.isTablet(getActivity()) && ViewUtils.isLandscapeOrientation(getActivity());
    }
}
