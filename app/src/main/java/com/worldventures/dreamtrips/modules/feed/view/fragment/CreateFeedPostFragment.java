package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateFeedPostPresenter;

import java.util.List;

import butterknife.OnClick;
import icepick.State;

@Layout(R.layout.layout_post)
public class CreateFeedPostFragment extends CreateEntityFragment<CreateFeedPostPresenter> implements CreateFeedPostPresenter.View {

    @State
    boolean pickerDisabled;

    @Override
    protected CreateFeedPostPresenter createPresenter(Bundle savedInstanceState) {
        return new CreateFeedPostPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArgs() != null && getArgs().isShowPickerImmediately()) {
            showMediaPicker();
            getArgs().setShowPickerImmediately(false);
        }
        updatePickerState();
    }

    @Override
    protected Route getRoute() {
        return Route.POST_CREATE;
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

    @OnClick(R.id.image)
    void onImage() {
        post.clearFocus();
        showMediaPicker();
    }

    @Override
    public void attachPhotos(List<UploadTask> images) {
        hideMediaPicker();
        //
        super.attachPhotos(images);
    }
}
