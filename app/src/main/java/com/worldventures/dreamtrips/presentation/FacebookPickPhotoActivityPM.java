package com.worldventures.dreamtrips.presentation;

import com.worldventures.dreamtrips.core.navigation.State;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class FacebookPickPhotoActivityPM extends BasePresentation<BaseActivityPresentation.View> {

    public FacebookPickPhotoActivityPM(View view) {
        super(view);
    }

    public void create() {
        fragmentCompass.add(State.PICK_FB_ALBUM);
    }

}
