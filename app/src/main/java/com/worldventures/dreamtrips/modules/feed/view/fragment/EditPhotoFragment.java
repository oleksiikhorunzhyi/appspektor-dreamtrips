package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.presenter.EditPhotoPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;

import butterknife.InjectView;

@Layout(R.layout.layout_post)
public class EditPhotoFragment extends ActionEntityFragment<EditPhotoPresenter, EditPhotoBundle> implements EditPhotoPresenter.View {

   @InjectView(R.id.image) ImageView image;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      image.setVisibility(View.GONE);
   }

   @Override
   protected int getPostButtonText() {
      return R.string.update;
   }

   @Override
   protected Route getRoute() {
      return Route.EDIT_PHOTO;
   }

   @Override
   protected EditPhotoPresenter createPresenter(Bundle savedInstanceState) {
      return new EditPhotoPresenter(getArgs().getPhoto());
   }

   @Override
   public void setText(String text) {
      //nothing to do
   }

}
