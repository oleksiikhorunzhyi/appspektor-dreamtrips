package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;

public abstract class CreateEntityFragment<PM extends CreateEntityPresenter> extends ActionEntityFragment<PM, CreateEntityBundle>
        implements CreateEntityPresenter.View {

    @InjectView(R.id.picker_container)
    ViewGroup pickerContainer;

    @State
    boolean imageFromArgsAlreadyAttached;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        post.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) hideMediaPicker();
            else name.requestFocus();
        });
        pickerContainer.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {

            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                backStackDelegate.setListener(() -> onBack());
            }
        });
        //
        attachImages();
    }

    @Override
    public void cancel() {
        pickerContainer.setOnHierarchyChangeListener(null);
        super.cancel();
    }

    @Override
    protected int getPostButtonText() {
        return R.string.post;
    }

    @Override
    public void showProgress() {
        shadow.setVisibility(View.VISIBLE);
        fabProgress.setVisibility(View.VISIBLE);
        fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
        fabProgress.setIndeterminate(true);
        fabProgress.showProgress(true);
        int color = getResources().getColor(R.color.bucket_blue);
        circleView.setColor(color);
    }

    @Override
    public void hideProgress() {
        fabProgress.setVisibility(View.GONE);
        shadow.setVisibility(View.GONE);
    }

    @Override
    public void imageError() {
        fabProgress.showProgress(false);
        fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
        int color = getResources().getColor(R.color.bucket_red);
        circleView.setColor(color);
    }

    @OnClick(R.id.cancel_action)
    protected void onPhotoCancel() {
        getPresenter().cancelClicked();
    }

    @OnClick(R.id.fab_progress)
    void onProgressClick() {
        getPresenter().onProgressClicked();
    }

    protected void showMediaPicker() {
        router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.picker_container)
                .data(new PickerBundle(getPresenter().getMediaRequestId()))
                .build());
    }

    protected void hideMediaPicker() {
        router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forRemoval()
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.picker_container)
                .build());
    }

    protected void attachImages() {
        if (!imageFromArgsAlreadyAttached && getMediaAttachment() != null) {
            getPresenter().attachImages(getMediaAttachment());
            imageFromArgsAlreadyAttached = true;
        }
    }

    private MediaAttachment getMediaAttachment() {
        return getArgs() != null && getArgs().getMediaAttachment() != null ? getArgs().getMediaAttachment() : null;
    }
}
