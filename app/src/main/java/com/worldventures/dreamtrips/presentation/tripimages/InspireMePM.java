package com.worldventures.dreamtrips.presentation.tripimages;

import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import org.robobinding.annotation.PresentationModel;

import java.util.List;

import retrofit.Callback;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

@PresentationModel
public class InspireMePM extends TripImagesListPM<Photo> {
    public InspireMePM(View view) {
        super(view, Type.INSPIRE_ME);
    }

    @Override
    public void loadPhotos(int perPage, int page, Callback<List<Photo>> callback) {
        dreamTripsApi.getInspirationsPhotos(perPage, page, callback);
    }


}