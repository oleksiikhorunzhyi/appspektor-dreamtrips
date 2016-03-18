package com.worldventures.dreamtrips.modules.common.view.util;

import android.os.Bundle;
import android.view.View;

import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;

import java.util.List;

public class PhotoPickerDelegate {

    private PhotoPickerLayout photoPickerLayout;
    //
    private PhotoPickerLayout.OnDoneClickListener onDoneClickListener;
    private SelectedPhotosProvider selectedPhotosProvider;

    public boolean isMultiPickEnabled() {
        return photoPickerLayout.isMultiPickEnabled();
    }

    public int getPickLimit() {
        return photoPickerLayout.getPickLimit();
    }

    public void attachScrollableView(View view) {
        photoPickerLayout.setScrollableView(view);
    }

    public void openFacebookAlbums() {
        photoPickerLayout.openFacebookAlbums();
    }

    public void openFacebookPhoto(Bundle bundle) {
        photoPickerLayout.openFacebookPhoto(bundle);
    }

    public void updatePickedItemsCount(int count) {
        photoPickerLayout.updatePickedItemsCount(count);
    }

    public void setupPhotoPickerLayout(PhotoPickerLayout photoPickerLayout) {
        this.photoPickerLayout = photoPickerLayout;
    }

    public void onDone() {
        if (onDoneClickListener != null && selectedPhotosProvider != null)
            onDoneClickListener.onDone(selectedPhotosProvider.provideSelectedPhotos(), selectedPhotosProvider.getType());
    }

    public void setOnDoneClickListener(PhotoPickerLayout.OnDoneClickListener onDoneClickListener) {
        this.onDoneClickListener = onDoneClickListener;
    }

    public void setSelectedPhotosProvider(SelectedPhotosProvider selectedPhotosProvider) {
        this.selectedPhotosProvider = selectedPhotosProvider;
    }

    public interface SelectedPhotosProvider {

        List provideSelectedPhotos();

        int getType();
    }

}
