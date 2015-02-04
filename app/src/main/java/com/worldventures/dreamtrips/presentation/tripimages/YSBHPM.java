package com.worldventures.dreamtrips.presentation.tripimages;

import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import org.robobinding.annotation.PresentationModel;

import java.util.List;

import retrofit.Callback;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

@PresentationModel
public class YSBHPM extends TripImagesListPM<Photo> {
    public YSBHPM(View view) {
        super(view, Type.YOU_SHOULD_BE_HERE);
    }

    @Override
    public void loadPhotos(int perPage, int page, Callback<List<Photo>> callback) {
        dreamTripsApi.getYouShoulBeHerePhotos(perPage, page, callback);
    }


}