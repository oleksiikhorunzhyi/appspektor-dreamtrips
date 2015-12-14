package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;

public class FixedListPhotosFullScreenPresenter extends TripImagesListPresenter<TripImagesListPresenter.View> {

    private ArrayList<IFullScreenObject> photos;

    public FixedListPhotosFullScreenPresenter(ArrayList<IFullScreenObject> photos, int userId) {
        super(TripImagesType.FIXED, userId);
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
