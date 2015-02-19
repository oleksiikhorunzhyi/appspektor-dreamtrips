package com.worldventures.dreamtrips.presentation.tripimages;

import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;


import java.util.List;

import retrofit.Callback;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.*;

public class UserImagesPM extends TripImagesListPM<Photo> {
    public UserImagesPM(View view) {
        super(view, Type.MEMBER_IMAGES);
    }

    @Override
    public void loadPhotos(int perPage,int page,Callback<List<Photo>> callback) {
        dreamTripsApi.getUserPhotos(perPage, page,callback);
    }
}
