package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.bundle.DocumentBundle;
import com.worldventures.dreamtrips.modules.infopages.presenter.WebViewFragmentPresenter;

@Layout(R.layout.fragment_webview)
public class DocumentFragment extends StaticInfoFragment<WebViewFragmentPresenter, DocumentBundle> {

   @Override
   protected String getURL() {
      return getArgs().getUrl();
   }

   @Override
   public void afterCreateView(View rootView) {
      ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getArgs().getTitle());

      webView.getSettings().setDomStorageEnabled(true);
      super.afterCreateView(rootView);
   }
}
