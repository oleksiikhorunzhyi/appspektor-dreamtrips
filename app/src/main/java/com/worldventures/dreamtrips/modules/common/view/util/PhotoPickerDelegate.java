package com.worldventures.dreamtrips.modules.common.view.util;

import android.os.Bundle;
import android.view.View;

import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;

import java.util.List;

public class PhotoPickerDelegate {

    private PhotoPickerLayout photoPickerLayout;
    //
    private SelectedPhotosProvider selectedPhotosProvider;
    private PhotoPickerLayout.OnDoneClickListener doneClickListener;
    private PhotoPickerLayout.OnDoneClickListener2 doneClickListener2;

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
        if (doneClickListener != null && selectedPhotosProvider != null) {
            List<ChosenImage> chosenImages = Queryable.from(selectedPhotosProvider.provideSelectedPhotos())
                    .map((Converter<BasePhotoPickerModel, ChosenImage>) element -> null)
                    .toList();
            doneClickListener.onDone(chosenImages, selectedPhotosProvider.getType());
            return;
        }
        if (doneClickListener2 != null && selectedPhotosProvider != null) {
            doneClickListener2.onDone(selectedPhotosProvider.provideSelectedPhotos(), selectedPhotosProvider.getType());
        }
    }

    public void setDoneClickListener(PhotoPickerLayout.OnDoneClickListener onDoneClickListener) {
        this.doneClickListener = onDoneClickListener;
    }

    public void setDoneClickListener2(PhotoPickerLayout.OnDoneClickListener2 doneClickListener2) {
        this.doneClickListener2 = doneClickListener2;
    }

    public void setSelectedPhotosProvider(SelectedPhotosProvider selectedPhotosProvider) {
        this.selectedPhotosProvider = selectedPhotosProvider;
    }

    public interface SelectedPhotosProvider {

        List<BasePhotoPickerModel> provideSelectedPhotos();

        int getType();
    }

}
