package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.layout_post)
public class CreateFeedPostFragment extends CreateEntityFragment {

   @Override
   public void onResume() {
      super.onResume();
      if (getArgs() != null && getArgs().isShowPickerImmediately()) {
         getPresenter().showMediaPicker();
         getArgs().setShowPickerImmediately(true);
      }
      updatePickerState();
   }
}
