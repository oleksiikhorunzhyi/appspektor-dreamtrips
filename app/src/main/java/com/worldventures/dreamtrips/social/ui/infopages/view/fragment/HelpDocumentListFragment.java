package com.worldventures.dreamtrips.social.ui.infopages.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.HelpDocumentListPresenter;

@Layout(R.layout.fragment_documents)
public class HelpDocumentListFragment extends DocumentListFragment<HelpDocumentListPresenter> {

   @Override
   protected HelpDocumentListPresenter createPresenter(Bundle savedInstanceState) {
      return new HelpDocumentListPresenter();
   }

   @Override
   public void trackViewFromViewPagerIfNeeded() {
      getPresenter().track();
   }
}
