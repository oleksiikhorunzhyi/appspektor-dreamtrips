package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.presenter.FeedbackImageAttachmentFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen.FullScreenPhotoFragment;

@Layout(R.layout.fragment_fullscreen_feedback_image_attachment)
public class FeedbackImageAttachmentFullscreenFragment extends FullScreenPhotoFragment<FeedbackImageAttachmentFullscreenPresenter,
      FeedbackImageAttachment> {

   @Override
   protected FeedbackImageAttachmentFullscreenPresenter createPresenter(Bundle savedInstanceState) {
      return new FeedbackImageAttachmentFullscreenPresenter((FeedbackImageAttachment) getArgs().getPhoto(), getArgs().getType());
   }
}



