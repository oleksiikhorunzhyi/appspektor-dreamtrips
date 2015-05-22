package com.worldventures.dreamtrips.modules.facebook.presenter;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;


public class FacebookPhotoPresenter extends Presenter<FacebookPhotoPresenter.View> {

    public void onBackAction() {
        fragmentCompass.pop();
    }

    public void onPhotoChosen(ChosenImage image) {
        view.preFinishProcessing(image);
    }

    public interface View extends Presenter.View {
        void preFinishProcessing(ChosenImage image);
    }
}
