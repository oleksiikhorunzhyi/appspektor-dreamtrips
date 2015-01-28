package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.model.TripImage;

import org.robobinding.annotation.PresentationModel;

/**
 * Created by 1 on 23.01.15.
 */
@PresentationModel
public class DetailedImagePagerFragmentPresentation extends BasePresentation<BasePresentation.View> {

    TripImage photo;


    public DetailedImagePagerFragmentPresentation(View view) {
        super(view);
    }

    public void setPhoto(TripImage photo) {
        this.photo = photo;
    }

    public TripImage getPhoto() {
        return photo;
    }

}

