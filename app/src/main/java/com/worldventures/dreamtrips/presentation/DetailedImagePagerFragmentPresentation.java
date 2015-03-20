package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.model.TripImage;


/**
 * Created by 1 on 23.01.15.
 */
public class DetailedImagePagerFragmentPresentation extends BasePresentation<BasePresentation.View> {

    TripImage photo;


    public DetailedImagePagerFragmentPresentation(View view) {
        super(view);
    }

    public TripImage getPhoto() {
        return photo;
    }

    public void setPhoto(TripImage photo) {
        this.photo = photo;
    }

}

