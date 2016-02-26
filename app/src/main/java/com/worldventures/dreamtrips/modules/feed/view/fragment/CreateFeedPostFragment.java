package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateFeedPostPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;

@Layout(R.layout.layout_post)
public class CreateFeedPostFragment extends CreateEntityFragment<CreateFeedPostPresenter> implements CreateFeedPostPresenter.View {

    @InjectView(R.id.photo_picker)
    PhotoPickerLayout photoPickerLayout;

    @State
    boolean pickerDisabled;

    private WeakHandler handler;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        //
        handler = new WeakHandler();
        //
        inject(photoPickerLayout);
        photoPickerLayout.setup(getChildFragmentManager(), false);
        photoPickerLayout.hidePanel();
        photoPickerLayout.setOnDoneClickListener((chosenImages, type) -> getPresenter().attachImages(chosenImages, type));
    }

    @Override
    protected CreateFeedPostPresenter createPresenter(Bundle savedInstanceState) {
        return new CreateFeedPostPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePickerState();
    }

    @Override
    protected void setupTextField() {
        super.setupTextField();
        post.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && photoPickerLayout.isPanelVisible())
                handler.postDelayed(photoPickerLayout::hidePanel, 250);
            else if (!hasFocus)
                name.requestFocus();
        });
    }

    @Override
    protected Route getRoute() {
        return Route.POST_CREATE;
    }

    @Override
    public void attachPhoto(Uri uri) {
        photoPickerLayout.hidePanel();
        //
        super.attachPhoto(uri);
    }

    @Override
    public void enableImagePicker() {
        pickerDisabled = false;
        updatePickerState();
    }

    @Override
    public void disableImagePicker() {
        pickerDisabled = true;
        updatePickerState();
    }

    protected void updatePickerState() {
        image.setEnabled(!pickerDisabled);
    }

    @Override
    protected boolean onBackPressed() {
        if (photoPickerLayout.isPanelVisible()) {
            photoPickerLayout.hidePanel();
            return true;
        }
        return super.onBackPressed();
    }

    @OnClick(R.id.image)
    void onImage() {
        if (photoPickerLayout.isPanelVisible()) {
            photoPickerLayout.hidePanel();
        } else {
            post.clearFocus();
            photoPickerLayout.showPanel();
        }
    }

    @OnClick(R.id.cancel_action)
    void onPhotoCancel() {
        getPresenter().removeImage();
    }
}
