package com.worldventures.dreamtrips.presentation;

import com.kbeanie.imagechooser.api.ChosenImage;


public class FacebookPhotoFragmentPM extends BasePresentation<FacebookPhotoFragmentPM.View> {
    public FacebookPhotoFragmentPM(View view) {
        super(view);
    }

    public void onBackAction() {
        fragmentCompass.pop();
    }

    public void onPhotoChosen(ChosenImage image) {
        view.preFinishProcessing(image);
    }

    public static interface View extends BasePresentation.View {
        void preFinishProcessing(ChosenImage image);
    }
}
