package com.worldventures.dreamtrips.modules.common.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.feed.model.AttachPhotoModel;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.presenter.GalleryPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.AttachPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoGalleryCell;
import com.worldventures.dreamtrips.modules.feed.view.util.GridAutofitLayoutManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class DtGalleryFragment extends BasePickerFragment<GalleryPresenter> implements GalleryPresenter.View {

    @Override
    protected void registerCells() {
        adapter.registerCell(PhotoGalleryModel.class, PhotoGalleryCell.class);
        adapter.registerCell(AttachPhotoModel.class, AttachPhotoCell.class);
    }

    @Override
    protected GalleryPresenter createPresenter(Bundle savedInstanceState) {
        return new GalleryPresenter();
    }

    @Override
    public void addItems(List<BasePhotoPickerModel> items) {
        adapter.addItem(new AttachPhotoModel(AttachPhotoModel.CAMERA, R.drawable.ic_picker_camera,
                R.string.camera, R.color.share_camera_color));
        adapter.addItem(new AttachPhotoModel(AttachPhotoModel.FACEBOOK, R.drawable.fb_logo,
                R.string.add_from_facebook, R.color.facebook_color));
        super.addItems(items);
    }

    @Override
    public void openFacebookAlbums() {
        photoPickerDelegate.openFacebookAlbums();
    }
}
