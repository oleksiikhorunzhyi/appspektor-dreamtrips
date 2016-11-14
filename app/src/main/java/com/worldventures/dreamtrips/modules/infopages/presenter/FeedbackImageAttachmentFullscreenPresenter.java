package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenPresenter;

public class FeedbackImageAttachmentFullscreenPresenter extends FullScreenPresenter<FeedbackImageAttachment, FullScreenPresenter.View> {

   public FeedbackImageAttachmentFullscreenPresenter(FeedbackImageAttachment photo, TripImagesType type) {
      super(photo, type);
   }
}
