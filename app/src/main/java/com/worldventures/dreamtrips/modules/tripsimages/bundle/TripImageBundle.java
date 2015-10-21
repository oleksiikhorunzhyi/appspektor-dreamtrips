package com.worldventures.dreamtrips.modules.tripsimages.bundle;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.ArrayList;


public class TripImageBundle {

    TripImagesListFragment.Type type;
    boolean fullScreenMode;
    int userId;
    ArrayList<IFullScreenObject> photos;
    int currentPhotosPosition;

    public TripImageBundle(TripImagesListFragment.Type type, boolean fullScreenMode, int userId, ArrayList<IFullScreenObject> photos, int currentPhotosPosition) {
        this.type = type;
        this.fullScreenMode = fullScreenMode;
        this.userId = userId;
        this.photos = photos;
        this.currentPhotosPosition = currentPhotosPosition;
    }

    public TripImagesListFragment.Type getType() {
        return type;
    }

    public boolean isFullScreenMode() {
        return fullScreenMode;
    }

    public int getUserId() {
        return userId;
    }

    public ArrayList<IFullScreenObject> getPhotos() {
        return photos;
    }

    public int getCurrentPhotosPosition() {
        return currentPhotosPosition;
    }
}
