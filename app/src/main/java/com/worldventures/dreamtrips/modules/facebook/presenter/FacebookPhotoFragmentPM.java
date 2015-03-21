package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;


public class FacebookPhotoFragmentPM extends BasePresenter<FacebookPhotoFragmentPM.View> {
    public FacebookPhotoFragmentPM(View view) {
        super(view);
    }

    public void onBackAction() {
        fragmentCompass.pop();
    }

    public void onPhotoChosen(ChosenImage image) {
        view.preFinishProcessing(image);
    }

    public static interface View extends BasePresenter.View {
        void preFinishProcessing(ChosenImage image);
    }
}
