package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.BookItPresenter;

import java.util.Map;

@Layout(R.layout.fragment_webview_with_overlay)
@ComponentPresenter.ComponentTitle(R.string.book_it)
public class BookItFragment extends BundleUrlFragment<BookItPresenter> {

   @Override
   protected BookItPresenter createPresenter(Bundle savedInstanceState) {
      return new BookItPresenter(getArgs().getUrl());
   }

   @Override
   public void reload(@NonNull String url, Map<String, String> headers) {
      webView.loadUrl("about:blank");
      load(url, headers);
   }
}
