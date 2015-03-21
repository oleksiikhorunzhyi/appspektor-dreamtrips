package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;


public class DetailedImagePagerFragmentPresenter extends BasePresenter<BasePresenter.View> {

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

