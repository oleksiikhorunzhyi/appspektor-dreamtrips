package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;

public class FixedPhotoFsPresenter extends TripImagesListPresenter {

    private ArrayList<IFullScreenObject> photos;

    public FixedPhotoFsPresenter(ArrayList<IFullScreenObject> photos) {
        super(TripImagesListFragment.Type.FIXED_LIST);
        this.photos = photos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onInjected() {
        db.savePhotoEntityList(type, photos);
        super.onInjected();
    }

    @Override
    public TripImagesRoboSpiceController getTripImagesRoboSpiceController() {
        return null;
    }

}
