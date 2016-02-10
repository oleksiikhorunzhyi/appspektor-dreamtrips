package com.worldventures.dreamtrips.modules.common.view.custom;

import android.support.v4.app.FragmentManager;

import timber.log.Timber;

public class PhotoPickerLayoutDelegate {

    private PhotoPickerLayout photoPickerLayout;

    public PhotoPickerLayoutDelegate() {
    }

    public void setPhotoPickerLayout(PhotoPickerLayout photoPickerLayout) {
        this.photoPickerLayout = photoPickerLayout;
    }

    public void initPicker(FragmentManager fragmentManager) {
        initPicker(fragmentManager, false, true);
    }

    public void initPicker(FragmentManager fragmentManager, boolean multiPickEnabled) {
        initPicker(fragmentManager, multiPickEnabled, true);
    }

    /**
     * Init picker and attach it to provided container
     *
     * @param fragmentManager  FragmentManager to init picker
     * @param multiPickEnabled default value is {false}
     * @param isVisible        default value is {true}
     */
    public void initPicker(FragmentManager fragmentManager, boolean multiPickEnabled, boolean isVisible) {
        photoPickerLayout.setup(fragmentManager, multiPickEnabled, isVisible);
    }

    public void setPhotoPickerListener(PhotoPickerLayout.PhotoPickerListener listener) {
        if (photoPickerLayout != null) photoPickerLayout.setPhotoPickerListener(listener);
        else Timber.d("Photo picker was not initialized");
    }

    public boolean isPanelVisible() {
        return photoPickerLayout != null && photoPickerLayout.isPanelVisible();
    }

    public void setOnDoneClickListener(PhotoPickerLayout.OnDoneClickListener onDoneClickListener) {
        photoPickerLayout.setOnDoneClickListener(onDoneClickListener);
    }

    public void showPicker() {
        if (photoPickerLayout != null) photoPickerLayout.showPanel();
        else Timber.d("Photo picker was not initialized");
    }

    public void hidePicker() {
        if (photoPickerLayout != null) photoPickerLayout.hidePanel();
        else Timber.d("Photo picker was not initialized");
    }
}
