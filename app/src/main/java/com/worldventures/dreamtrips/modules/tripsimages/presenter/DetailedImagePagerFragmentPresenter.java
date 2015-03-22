package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;


public class DetailedImagePagerFragmentPresenter extends Presenter<Presenter.View> {

    TripImage photo;


    public DetailedImagePagerFragmentPresenter(View view) {
        super(view);
    }

    public TripImage getPhoto() {
        return photo;
    }

    public void setPhoto(TripImage photo) {
        this.photo = photo;
    }

}

