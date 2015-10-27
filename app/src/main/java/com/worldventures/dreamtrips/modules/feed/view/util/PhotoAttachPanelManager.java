package com.worldventures.dreamtrips.modules.feed.view.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.AttachPhotoModel;
import com.worldventures.dreamtrips.modules.feed.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.AttachPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoGalleryCell;
import com.worldventures.dreamtrips.modules.membership.view.util.WrapContentLinearLayoutManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoAttachPanelManager {

    @InjectView(R.id.photo_picker)
    RecyclerView photoPicker;

    public PhotoAttachPanelManager(View view) {
        ButterKnife.inject(this, view);
    }

    public void setup(Injector injector, List<PhotoGalleryModel> photos) {
        BaseArrayListAdapter adapter = new BaseArrayListAdapter<>(photoPicker.getContext(), injector);
        adapter.registerCell(PhotoGalleryModel.class, PhotoGalleryCell.class);
        adapter.registerCell(AttachPhotoModel.class, AttachPhotoCell.class);
        adapter.addItem(new AttachPhotoModel(AttachPhotoModel.CAMERA, R.drawable.ic_camera, R.string.camera, R.color.share_camera_color));
        adapter.addItem(new AttachPhotoModel(AttachPhotoModel.FACEBOOK, R.drawable.fb_logo, R.string.add_from_facebook, R.color.facebook_color));
        adapter.addItems(photos);

        photoPicker.setLayoutManager(new WrapContentLinearLayoutManager(photoPicker.getContext(), LinearLayoutManager.HORIZONTAL, false));
        photoPicker.setAdapter(adapter);
    }

    public void showPanel() {
        photoPicker.setVisibility(View.VISIBLE);
    }

    public void hidePanel() {
        photoPicker.setVisibility(View.GONE);
    }

    public boolean isPanelVisible() {
        return photoPicker.getVisibility() == View.VISIBLE;
    }
}
