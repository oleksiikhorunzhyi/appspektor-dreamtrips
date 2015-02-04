package com.worldventures.dreamtrips.presentation.tripimages;

import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListFragmentPM;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import org.robobinding.annotation.PresentationModel;

import java.util.List;

import retrofit.Callback;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.*;

@PresentationModel
public class UserImagesPM extends TripImagesListFragmentPM<Photo> {
    public UserImagesPM(View view) {
        super(view, Type.MEMBER_IMAGES);
    }

    @Override
    public void loadPhotos(int perPage,int page,Callback<List<Photo>> callback) {
        dreamTripsApi.getUserPhotos(perPage, page,callback);
    }
}
