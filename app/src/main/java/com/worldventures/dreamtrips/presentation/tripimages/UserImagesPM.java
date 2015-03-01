package com.worldventures.dreamtrips.presentation.tripimages;

import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public class UserImagesPM extends TripImagesListPM<Photo> {
    public UserImagesPM(View view) {
        super(view, Type.MEMBER_IMAGES);
    }


    @Override
    public void loadPhotos(int perPage, int page, RequestListener<ArrayList<Photo>> callback) {
        dreamSpiceManager.execute(new DreamTripsRequest.GetUserPhotos(perPage, page), callback);
    }
}
