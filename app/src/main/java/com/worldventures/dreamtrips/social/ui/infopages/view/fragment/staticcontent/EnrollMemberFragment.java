package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollMemberPresenter;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;

@Layout(R.layout.fragment_webview)
public class EnrollMemberFragment extends AuthorizedStaticInfoFragment<EnrollMemberPresenter, UrlBundle>
      implements EnrollMemberPresenter.View {

   @Override
   protected EnrollMemberPresenter createPresenter(Bundle savedInstanceState) {
      return new EnrollMemberPresenter();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      webView.getSettings().setLoadWithOverviewMode(true);
      webView.getSettings().setUseWideViewPort(true);
   }

   @Override
   public void showPermissionExplanationText(String[] permissions) {
      new MaterialDialog.Builder(getContext())
            .content(R.string.permission_location_for_localization_web_page)
            .positiveText(R.string.dialog_ok)
            .negativeText(R.string.dialog_cancel)
            .onPositive((materialDialog, dialogAction) -> getPresenter().recheckPermissionAccepted(true))
            .onNegative((materialDialog, dialogAction) -> getPresenter().recheckPermissionAccepted(false))
            .cancelable(false)
            .show();
   }

   @Override
   public void showPermissionDenied(String[] permissions) {
      //do nothing
   }

   @Override
   protected void trackViewFromViewPagerIfNeeded() {
      getPresenter().sendAnalyticsEnrollViewedAction();
   }
}
