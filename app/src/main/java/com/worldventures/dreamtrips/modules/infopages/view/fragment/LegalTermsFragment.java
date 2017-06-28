package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.LegalTermsPresenter;


@Layout(R.layout.fragment_documents)
public class LegalTermsFragment extends DocumentListFragment<LegalTermsPresenter> implements Presenter.View {

   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      getPresenter().track();
   }

   @Override
   protected LegalTermsPresenter createPresenter(Bundle savedInstanceState) {
      return new LegalTermsPresenter();
   }
}
