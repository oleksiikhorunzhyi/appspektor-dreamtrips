package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.webkit.WebSettings;

import com.worldventures.dreamtrips.social.ui.infopages.presenter.AuthorizedStaticInfoPresenter;

import java.net.HttpURLConnection;

public abstract class AuthorizedStaticInfoFragment<PR extends AuthorizedStaticInfoPresenter, P extends Parcelable> extends StaticInfoFragment<PR, P>
      implements AuthorizedStaticInfoPresenter.View {

   @Override
   public void afterCreateView(View rootView) {
      webView.getSettings().setDomStorageEnabled(true);
      webView.getSettings().setAppCachePath("/data/data/com.worldventures.dreamtrips/cache");
      webView.getSettings().setAppCacheEnabled(true);
      webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
      //Mixed Content: The page at 'https://secure.worldventures.biz/(S(2l0jz1vwma03azykskghv03o))/Checkout/ShoppingCart.aspx?did=ODg4ODg4&pn=V1ZCaXoy' was loaded over HTTPS, but requested an insecure image 'http://secure.worldventures.biz/(S(2l0jz1vwma03azykskghv03o))/Controls/ImageCreator.aspx?Id=2'. This request has been blocked; the content must be served over HTTPS."
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
      }//otherwise enabled  by default
      super.afterCreateView(rootView);
   }

   @Override
   protected void onReceivedHttpError(int errorCode) {
      super.onReceivedHttpError(errorCode);
      if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) getPresenter().reLogin();
   }

   @Override
   protected PR createPresenter(Bundle savedInstanceState) {
      return (PR) new AuthorizedStaticInfoPresenter(getURL());
   }
}
