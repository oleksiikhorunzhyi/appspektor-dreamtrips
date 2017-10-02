package com.worldventures.dreamtrips.social.ui.infopages.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.utils.AnalyticsInteractorProxy;
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.ViewBookTravelAnalytics;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.StaticInfoFragment;

import javax.inject.Inject;

public class OtaPresenter extends AuthorizedStaticInfoPresenter {

   @Inject AnalyticsInteractorProxy analyticsInteractorProxy;

   public OtaPresenter(String url) {
      super(url);
   }

   @Override
   public void pageLoaded(String url) {
      super.pageLoaded(url);

      if (!TextUtils.equals(StaticInfoFragment.BLANK_PAGE, url)) {
         analyticsInteractorProxy.sendCommonAnalytic(new ViewBookTravelAnalytics());
      }
   }
}
