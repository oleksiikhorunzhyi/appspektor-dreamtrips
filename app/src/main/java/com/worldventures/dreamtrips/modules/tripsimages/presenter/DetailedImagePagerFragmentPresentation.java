package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.core.model.TripImage;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresentation;


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

