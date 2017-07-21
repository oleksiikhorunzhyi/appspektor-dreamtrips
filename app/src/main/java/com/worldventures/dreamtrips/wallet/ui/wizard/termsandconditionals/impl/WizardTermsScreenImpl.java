package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.impl;


import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.ViewProgressView;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsScreen;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class WizardTermsScreenImpl extends WalletBaseController<WizardTermsScreen, WizardTermsPresenter> implements WizardTermsScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.container_layout_agreement) ViewGroup userAgreementViewGroup;
   @InjectView(R.id.termsView) WebView termsView;
   @InjectView(R.id.wallet_wizard_terms_and_conditions_agree_btn) Button agreeBtn;
   @InjectView(R.id.pb) View pb;

   @Inject WizardTermsPresenter presenter;

   private MaterialDialog errorDialog;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      agreeBtn.setVisibility(GONE);
      userAgreementViewGroup.setLayoutTransition(new LayoutTransition());
      toolbar.setNavigationOnClickListener(v -> getPresenter().onBack());
      termsView.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageFinished(WebView view, String url) {
            if (view.getProgress() == 100) {
               pb.setVisibility(GONE);
               agreeBtn.setVisibility(VISIBLE);
            }
         }

         @Override
         public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            pb.setVisibility(GONE);
            view.setVisibility(INVISIBLE);
            showLoadTermsError();
         }

         @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.setVisibility(VISIBLE);
         }

         @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:")) {
               Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
               getContext().startActivity(Intent.createChooser(emailIntent, getString(R.string.email_app_choose_dialog_title)));
               view.reload();
               return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
         }
      });
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_wizard_termsandconditions, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   public void showLoadTermsError() {
      buildErrorDialog((dialog, which) -> termsView.reload());
   }

   @Override
   protected void onDetach(@NonNull View view) {
      if (errorDialog != null) {
         errorDialog.dismiss();
         errorDialog = null;
      }

      super.onDetach(view);
   }

   @Override
   public WizardTermsPresenter getPresenter() {
      return presenter;
   }

   @Override
   public void showTerms(String url) {
      termsView.loadUrl("about:blank");
      termsView.loadUrl(url);
   }

   public void failedToLoadTerms() {
      buildErrorDialog((dialog, which) -> getPresenter().loadTerms());
   }

   @Override
   public OperationView<FetchTermsAndConditionsCommand> termsOperationView() {
      //noinspection unchecked cast
      return new ComposableOperationView<>(new ViewProgressView<>(pb), null, this);
   }

   private void buildErrorDialog(MaterialDialog.SingleButtonCallback retryAction) {
      errorDialog = new MaterialDialog.Builder(getContext()).title(R.string.wallet_error_label)
            .content(R.string.wallet_terms_and_conditions_load_failed)
            .positiveText(R.string.wallet_retry_label)
            .onPositive(retryAction)
            .show();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(getView());
   }

   @OnClick(R.id.wallet_wizard_terms_and_conditions_agree_btn)
   void onAcceptClicked() {
      getPresenter().acceptTermsPressed();
   }

   @Override
   public void showError(Object o, Throwable throwable) {
      failedToLoadTerms();
   }

   @Override
   public boolean isErrorVisible() {
      return errorDialog != null && errorDialog.isShowing();
   }

   @Override
   public void hideError() {
      if (errorDialog != null) errorDialog.dismiss();
   }
}
