package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;

import com.worldventures.dreamtrips.social.ui.feed.bundle.EditPostBundle;
import com.worldventures.dreamtrips.social.ui.feed.presenter.ActionEntityPresenter;
import com.worldventures.dreamtrips.social.ui.feed.presenter.EditPostPresenter;

import butterknife.InjectView;

@Layout(R.layout.layout_post)
public class EditPostFragment extends ActionEntityFragment<EditPostPresenter, EditPostBundle> implements ActionEntityPresenter.View {

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
   protected EditPostPresenter createPresenter(Bundle savedInstanceState) {
      return new EditPostPresenter(getArgs().getPost());
   }
}
