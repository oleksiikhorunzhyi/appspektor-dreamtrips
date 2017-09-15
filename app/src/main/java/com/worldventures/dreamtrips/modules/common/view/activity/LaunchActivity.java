package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.core.SocialConnectionOverlay;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.view.SocialConnectionOverlayViewFactory;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.social.ui.infopages.StaticPageProvider;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

@Layout(R.layout.activity_launch)
public class LaunchActivity extends ActivityWithPresenter<LaunchActivityPresenter> implements LaunchActivityPresenter.View {

   @InjectView(R.id.splash_mode_holder) View splashModeHolder;
   @InjectView(R.id.et_username) DTEditText usernameEditText;
   @InjectView(R.id.et_password) DTEditText passwordEditText;
   @InjectView(R.id.btn_login) Button loginButton;
   @InjectView(R.id.login_progress) View loginProgress;
   @InjectView(R.id.content_layout) View contentView;

   @InjectView(R.id.login_edittexts_holder) View loginEditTextsHolder;
   @InjectView(R.id.login_mode_holder) View loginModeHolder;
   private SocialConnectionOverlay connectionOverlay;
   @Inject StaticPageProvider staticPageProvider;

   @Override
   protected LaunchActivityPresenter createPresentationModel(Bundle savedInstanceState) {
      return new LaunchActivityPresenter();
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      loginEditTextsHolder.getLayoutParams().width = ViewUtils.getMinSideSize(this);
   }

   @OnClick(R.id.iv_title)
   public void onTitleClick() {
      if (BuildConfig.DEBUG && !BuildConfig.FLAVOR.equals("prod")) {
         usernameEditText.setText("888888");
         passwordEditText.setText("travel1ns1de");
      }
   }

   @OnClick(R.id.btn_login)
   public void onLoginClick() {
      SoftInputUtil.hideSoftInputMethod(this);
      getPresentationModel().loginAction();
   }

   @OnClick(R.id.tw_forgot_member_id)
   void onForgotMemberIdClicked() {
      openInBrowser(staticPageProvider.getForgotMemberIdUrl());
   }

   @OnClick(R.id.tw_forgot_password)
   void onForgotPasswordClicked() {
      openInBrowser(staticPageProvider.getForgotPasswordUrl());
   }

   private void openInBrowser(String url) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
      if (intent.resolveActivity(getPackageManager()) != null) {
         startActivity(intent);
      }
   }

   @Override
   public String getUsername() {
      return usernameEditText.getText().toString();
   }

   @Override
   public String getUserPassword() {
      return passwordEditText.getText().toString();
   }

   @Override
   public void initConnectionOverlay(Observable<ConnectionState> connectionStateObservable, Observable<Void> stopper) {
      connectionOverlay = new SocialConnectionOverlay(new SocialConnectionOverlayViewFactory(this, contentView));
      connectionOverlay.startProcessingState(connectionStateObservable, stopper);
   }

   @Override
   public void openLogin() {
      splashModeHolder.setVisibility(View.GONE);
      loginModeHolder.setVisibility(View.VISIBLE);
   }

   @Override
   public void openSplash() {
      splashModeHolder.setVisibility(View.VISIBLE);
      loginModeHolder.setVisibility(View.GONE);
      dismissLoginProgress();
   }

   @Override
   public void openMain() {
      activityRouter.openMain();
      activityRouter.finish();
   }

   @Override
   public void showLoginProgress() {
      usernameEditText.clearFocus();
      usernameEditText.setEnabled(false);
      passwordEditText.clearFocus();
      passwordEditText.setEnabled(false);
      loginButton.setVisibility(View.GONE);
      loginButton.setClickable(false);
   }

   @Override
   public void informUser(String message) {
      runOnUiThread(() -> {
         MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
         builder.title(R.string.alert).content(message).positiveText(R.string.OK).show();
      });
   }

   @Override
   public void dismissLoginProgress() {
      new WeakHandler().postDelayed(() -> {
         if (loginButton != null) {
            usernameEditText.setEnabled(true);
            passwordEditText.setEnabled(true);
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setClickable(true);
         }
      }, 50);
   }

   @Override
   public void showLocalErrors(int userNameError, int passwordError) {
      if (userNameError != 0) {
         usernameEditText.setError(getString(userNameError));
      }

      if (passwordError != 0) {
         passwordEditText.setError(getString(passwordError));
      }
   }
}
