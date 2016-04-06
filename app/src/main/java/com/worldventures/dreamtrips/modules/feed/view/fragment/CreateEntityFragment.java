package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.bundle.PickerBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;

import butterknife.InjectView;
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
    public void updateItem(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    protected int getPostButtonText() {
        return R.string.post;
    }

    @Override
    public void onProgressClicked(UploadTask uploadTask) {
        super.onProgressClicked(uploadTask);
        getPresenter().startUpload(uploadTask);
    }

    @Override
    public void onRemoveClicked(UploadTask uploadTask) {
        super.onRemoveClicked(uploadTask);
        int position = getPresenter().removeImage(uploadTask);
        if (position != -1) adapter.notifyItemRemoved(position);
    }

    protected void showMediaPicker() {
        router.moveTo(Route.MEDIA_PICKER, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getChildFragmentManager())
                .containerId(R.id.picker_container)
                .data(new PickerBundle(getPresenter().getMediaRequestId(), 15))
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
