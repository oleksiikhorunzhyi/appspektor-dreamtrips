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

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.DiningItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketPhotosView;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Optional;

@Layout(R.layout.layout_bucket_item_details)
public class BucketDetailsFragment extends BaseFragment<BucketItemDetailsPresenter> implements BucketItemDetailsPresenter.View {

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

    @InjectView(R.id.diningName)
    TextView diningName;

    @InjectView(R.id.diningPriceRange)
    TextView diningPriceRange;

    @InjectView(R.id.diningAddress)
    TextView diningAddress;

    @InjectView(R.id.diningSite)
    TextView diningSite;

    @InjectView(R.id.diningPhone)
    TextView diningPhone;

    @InjectView(R.id.diningContainer)
    View diningContainer;

    @InjectView(R.id.diningDivider)
    View diningDivider;

    @Inject
    @ForActivity
    Provider<Injector> injector;

    WeakHandler handler = new WeakHandler();

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
        setForeignIntentAction();
    }

    @Override
    protected BucketItemDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketItemDetailsPresenter(getArguments());
    }

    @Override
    public void setCover(String medium, String original) {
        loadImage(medium, original);
    }

    private void loadImage(String lowUrl, String url) {
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequest.fromUri(lowUrl))
                .setImageRequest(ImageRequest.fromUri(url))
                .build();
        imageViewCover.setController(draweeController);
    }

    @OnClick(R.id.bucketItemEdit)
    protected void onEdit() {
        getPresenter().onEdit();
    }

    @OnClick(R.id.bucketItemShare)
    protected void onShare() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.action_share)
                .items(R.array.share_dialog_items)
                .itemsCallback((dialog, view, which, text) -> {
                    if (which == 0) {
                        getPresenter().onFbShare();
                    } else {
                        getPresenter().onTwitterShare();
                    }
                }).show();
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

    @OnCheckedChanged(R.id.checkBoxDone)
    protected void onCheckedChanged(boolean isChecked) {
        getPresenter().onStatusUpdated(isChecked);
    }

    @OnClick(R.id.bucketItemDelete)
    public void onDelete() {
        getPresenter().onDelete();
    }

    @OnClick(R.id.imageViewCover)
    protected void onCoverClicked() {
        getPresenter().onCoverClicked();
    }

    private void setForeignIntentAction() {
        diningSite.setOnClickListener(v -> {
            Intent intent = IntentUtils.browserIntent(diningSite.getText().toString());
            startActivity(intent);
        });
        diningPhone.setOnClickListener(v -> {
            Intent intent = IntentUtils.callIntnet(diningPhone.getText().toString());
            startActivity(intent);
        });
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
    public void setupDiningView(DiningItem diningItem) {
        if (diningItem != null) {
            setText(diningName, diningItem.getName());
            setText(diningPriceRange, diningItem.getPriceRange());
            setText(diningAddress, diningItem.getAddress());
            setText(diningPhone, diningItem.getPhoneNumber());
            setText(diningSite, diningItem.getUrl());
            if (TextUtils.isEmpty(diningItem.getUrl()) && TextUtils.isEmpty(diningItem.getPhoneNumber())) {
                diningDivider.setVisibility(View.GONE);
            } else {
                diningDivider.setVisibility(View.VISIBLE);
            }
        } else {
            diningContainer.setVisibility(View.GONE);
        }

    }

    @Override
    public void setCategory(String category) {
        setText(textViewCategory, category);
    }

    @Override
    public void setPlace(String place) {
        setText(textViewPlace, place);
    }

    private void setText(TextView view, String text) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(text);
        }
    }

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
        getActivity().findViewById(R.id.container_details_floating).setVisibility(View.VISIBLE);
    }

    @Override
    public void updatePhotos() {
        bucketPhotosView.init(this, injector, BucketPhotosView.Type.DETAILS);
        bucketPhotosView.multiSelectAvailable(true);
        bucketPhotosView.setMakePhotoImageCallback(getPresenter().getPhotoChooseCallback());
        bucketPhotosView.setFbImageCallback(getPresenter().getFbCallback());
        bucketPhotosView.setChooseImageCallback(getPresenter().getGalleryChooseCallback());
        bucketPhotosView.setMultiSelectPickCallback(getPresenter().getMultiSelectPickCallback());
    }

    @Override
    public void showDeletionDialog(BucketItem bucketItem) {
        new MaterialDialog.Builder(getActivity())
                .content(R.string.bucket_delete_dialog)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        getPresenter().deleteBucketItem(bucketItem);
                    }
                })
                .show();
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
