package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;


public class FacebookPhotoPresenter extends Presenter<FacebookPhotoPresenter.View> {
    public FacebookPhotoPresenter(View view) {
        super(view);
    }

    public void onBackAction() {
        fragmentCompass.pop();
    }

    public void onPhotoChosen(ChosenImage image) {
        view.preFinishProcessing(image);
    }

    public static interface View extends Presenter.View {
        void preFinishProcessing(ChosenImage image);
    }
}
