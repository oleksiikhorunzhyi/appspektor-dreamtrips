package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.bundle.EditPostBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.EditPostPresenter;

import butterknife.InjectView;

@Layout(R.layout.layout_post)
public class EditPostFragment extends ActionEntityFragment<EditPostPresenter, EditPostBundle> implements EditPostPresenter.View {

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
      return Route.EDIT_POST;
   }

   @Override
   protected EditPostPresenter createPresenter(Bundle savedInstanceState) {
      return new EditPostPresenter(getArgs().getPost());
   }
}
