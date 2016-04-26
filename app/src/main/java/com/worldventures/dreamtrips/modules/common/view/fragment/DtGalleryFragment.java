package com.worldventures.dreamtrips.modules.common.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.PickerIrregularPhotoModel;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.PickerIrregularPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoGalleryCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

@RuntimePermissions
public class DtGalleryFragment extends BasePickerFragment<GalleryPresenter> implements GalleryPresenter.View {
    @Override
    protected void registerCells() {
        adapter.registerCell(PhotoGalleryModel.class, PhotoGalleryCell.class);
        adapter.registerCell(PickerIrregularPhotoModel.class, PickerIrregularPhotoCell.class);
    }

    @Override
    protected int getPhotosType() {
        return PickImageDelegate.PICK_PICTURE;
    }

    @Override
    protected GalleryPresenter createPresenter(Bundle savedInstanceState) {
        return new GalleryPresenter();
    }

    @Override
    public void addItems(List<BasePhotoPickerModel> items) {
        adapter.addItem(new PickerIrregularPhotoModel(PickerIrregularPhotoModel.CAMERA, R.drawable.ic_picker_camera,
                R.string.camera, R.color.share_camera_color));
        adapter.addItem(new PickerIrregularPhotoModel(PickerIrregularPhotoModel.FACEBOOK, R.drawable.fb_logo,
                R.string.add_from_facebook, R.color.facebook_color));
        super.addItems(items);
    }

    @Override
    public void openFacebookAlbums() {
        photoPickerDelegate.openFacebookAlbums();
    }

    @Override
    public void checkPermissions() {
        DtGalleryFragmentPermissionsDispatcher.cameraPermissionWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        DtGalleryFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void cameraPermission() {
        getPresenter().openCamera();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showRationaleForCamera(PermissionRequest request) {
        Snackbar.make(getView(), R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void showDeniedForCamera() {
        Snackbar.make(getView(), R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show();
    }
}
