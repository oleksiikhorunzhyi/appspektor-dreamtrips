package com.worldventures.dreamtrips.presentation.tripimages;

import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public class InspireMePM extends TripImagesListPM<Inspiration> {
    public InspireMePM(View view) {
        super(view, Type.INSPIRE_ME);
    }

   /* @Override
    public void loadPhotos(int perPage, int page, Callback<List<Inspiration>> callback) {
      //  dreamTripsApi.getInspirationsPhotos(perPage, page, callback);
    }*/


    @Override
    public void loadPhotos(int perPage, int page, RequestListener<ArrayList<Inspiration>> callback) {
        dreamSpiceManager.execute(new DreamTripsRequest.GetInspireMePhotos(perPage, page), callback);

    }
}