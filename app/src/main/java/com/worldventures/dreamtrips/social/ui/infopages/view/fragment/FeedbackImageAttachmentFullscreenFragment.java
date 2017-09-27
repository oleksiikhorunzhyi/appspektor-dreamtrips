package com.worldventures.dreamtrips.social.ui.infopages.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.social.ui.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.custom.ImageryView;

import butterknife.InjectView;

@Layout(R.layout.fragment_fullscreen_feedback_image_attachment)
public class FeedbackImageAttachmentFullscreenFragment extends BaseFragmentWithArgs<Presenter, FeedbackImageAttachment> {

   @InjectView(R.id.iv_image) ImageryView imageryView;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      imageryView.setImageURI(getArgs().getOriginalFilePath());
   }

   @Override
   protected Presenter createPresenter(Bundle savedInstanceState) {
      return new Presenter();
   }
}



