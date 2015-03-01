package com.worldventures.dreamtrips.presentation.tripimages;

import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import java.util.ArrayList;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public class YSBHPM extends TripImagesListPM<Photo> {
    public YSBHPM(View view) {
        super(view, Type.YOU_SHOULD_BE_HERE);
    }


    @Override
    public void loadPhotos(int perPage, int page, RequestListener<ArrayList<Photo>> callback) {
        dreamSpiceManager.execute(new DreamTripsRequest.GetYSBHPhotos(perPage, page), callback);

    }
}