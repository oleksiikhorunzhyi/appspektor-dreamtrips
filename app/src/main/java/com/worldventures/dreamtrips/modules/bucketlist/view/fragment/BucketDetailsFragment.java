package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketPhotosView;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.layout_detailed_bucket_item)
public class BucketDetailsFragment extends BaseFragment<BucketItemDetailsPresenter> implements BucketItemDetailsPresenter.View {

    @Inject
    @ForActivity
    Provider<Injector> injector;

    @InjectView(R.id.imageViewCover)
    protected SimpleDraweeView imageViewCover;

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

    @Optional
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @InjectView(R.id.bucket_photos)
    protected BucketPhotosView bucketPhotosView;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        imageViewCover.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.0f));
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
            toolbar.getBackground().setAlpha(0);
        }
    }

    @Override
    protected BucketItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketItemDetailsPresenter(getArguments());
    }

    @Override
    public void setCover() {
        String medium = getPresenter().getMediumResUrl();
        String original = getPresenter().getHighResUrl();
        loadImage(medium, original);
    }

    private void loadImage(String lowUrl, String url) {
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(lowUrl))
                .setImageRequest(ImageRequest.fromUri(url))
                .build();
        imageViewCover.setController(draweeController);
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
        if (TextUtils.isEmpty(description)) {
            textViewDescription.setVisibility(View.GONE);
        } else {
            textViewDescription.setVisibility(View.VISIBLE);
            textViewDescription.setText(description);
        }
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

    @OnCheckedChanged(R.id.checkBoxDone)
    protected void onCheckedChanged(boolean isChecked) {
        getPresenter().onStatusUpdated(isChecked);
    }

    @OnClick(R.id.imageViewCover)
    protected void onCoverClicked() {
        getPresenter().openFullScreen(0);
    }

    @Override
    public void disableCheckbox() {
        checkBox.setEnabled(false);
    }

    @Override
    public void enableCheckbox() {
        checkBox.setEnabled(true);
    }

    @Override
    public void setCategory(String category) {
        if (TextUtils.isEmpty(category)) {
            textViewCategory.setVisibility(View.GONE);
        } else {
            textViewCategory.setVisibility(View.VISIBLE);
            textViewCategory.setText(category);
        }
    }

    WeakHandler handler = new WeakHandler();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        eventBus.postSticky(new ActivityResult(requestCode, resultCode, data));
    }

    public void onEvent(ActivityResult event) {
        eventBus.removeStickyEvent(event);
        handler.post(() -> bucketPhotosView.onActivityResult(event.requestCode, event.resultCode, event.data));
    }

    @Override
    public void done() {
        getActivity().onBackPressed();
    }

    @Override
    public IBucketPhotoView getBucketPhotosView() {
        return bucketPhotosView;
    }

    @Override
    public void showEditContainer() {
        getActivity().findViewById(R.id.container_edit).setVisibility(View.VISIBLE);
    }

    @Override
    public void updatePhotos() {
        bucketPhotosView.init(this, injector, BucketPhotosView.Type.DETAILS);
        bucketPhotosView.setSelectImageCallback(getPresenter().getPhotoChooseCallback());
        bucketPhotosView.setFbImageCallback(getPresenter().getFbCallback());
        bucketPhotosView.setChooseImageCallback(getPresenter().getGalleryChooseCallback());
    }

    public static class ActivityResult {
        public final int requestCode;
        public final int resultCode;
        public final Intent data;

        public ActivityResult(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }
    }
}
