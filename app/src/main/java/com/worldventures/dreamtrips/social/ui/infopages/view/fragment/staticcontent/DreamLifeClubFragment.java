package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;

import com.worldventures.dreamtrips.social.ui.infopages.presenter.DreamLifeClubPresenter;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;

public class DreamLifeClubFragment extends StaticInfoFragment<DreamLifeClubPresenter, UrlBundle> {

   @Override
   protected String getURL() {
      return provider.getDLCUrl();
   }

   @Override
   protected DreamLifeClubPresenter createPresenter(Bundle savedInstanceState) {
      return new DreamLifeClubPresenter(getURL());
   }
}
