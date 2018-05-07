package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.worldventures.core.modules.infopages.bundle.DocumentBundle;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.DocumentPresenter;

@Layout(R.layout.fragment_webview)
public class DocumentFragment extends StaticInfoFragment<DocumentPresenter, DocumentBundle> implements DocumentPresenter.View {

   @Override
   public void afterCreateView(View rootView) {
      ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getArgs().getDocument().getName());
      webView.getSettings().setDomStorageEnabled(true);
      super.afterCreateView(rootView);
   }

   @Override
   protected DocumentPresenter createPresenter(Bundle savedInstanceState) {
      return new DocumentPresenter(getArgs().getDocument(), getArgs().getAnalyticsAction());
   }

}
