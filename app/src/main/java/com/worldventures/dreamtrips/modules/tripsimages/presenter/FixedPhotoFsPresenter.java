package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;

public class FixedPhotoFsPresenter extends TripImagesListPresenter {

    private ArrayList<IFullScreenObject> photos;

    public FixedPhotoFsPresenter(ArrayList<IFullScreenObject> photos, int userId) {
        super(TripImagesListFragment.Type.FIXED_LIST, userId);
        this.photos = photos;
    }

    @Override
    protected void syncPhotosAndUpdatePosition() {
        super.photos.addAll(photos);
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return null;
    }

}
