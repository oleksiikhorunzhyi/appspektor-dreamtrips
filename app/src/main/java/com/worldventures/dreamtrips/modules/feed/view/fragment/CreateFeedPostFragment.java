package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.model.ImmutableVideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.VideoCreationCellDelegate;

@Layout(R.layout.layout_post)
public class CreateFeedPostFragment extends CreateEntityFragment {

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter.registerDelegate(ImmutableVideoCreationModel.class, new VideoCreationCellDelegate() {
         @Override
         public void onRemoveClicked(VideoCreationModel model) {
            getPresenter().removeVideo(model);
         }

         @Override
         public void onCellClicked(VideoCreationModel model) { }
      });

   }

   @Override
   public void onResume() {
      super.onResume();
      if (getArgs() != null && getArgs().isShowPickerImmediately()) {
         getPresenter().showMediaPicker();
         getArgs().setShowPickerImmediately(true);
      }
      updatePickerState();
   }

   @Override
   protected Route getRoute() {
      return Route.POST_CREATE;
   }
}
