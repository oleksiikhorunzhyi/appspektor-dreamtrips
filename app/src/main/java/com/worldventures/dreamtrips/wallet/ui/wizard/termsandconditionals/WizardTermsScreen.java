package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
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
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.ViewProgressView;

import butterknife.InjectView;
import butterknife.OnClick;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardTermsScreen extends WalletLinearLayout<WizardTermsPresenter.Screen, WizardTermsPresenter, WizardTermsPath> implements WizardTermsPresenter.Screen, ErrorView {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.container_layout_agreement) ViewGroup userAgreementViewGroup;
   @InjectView(R.id.termsView) WebView termsView;
   @InjectView(R.id.wallet_wizard_terms_and_conditions_agree_btn) Button agreeBtn;
   @InjectView(R.id.pb) View pb;

   private MaterialDialog errorDialog;

   public WizardTermsScreen(Context context) {
      super(context);
   }

   public WizardTermsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WizardTermsPresenter createPresenter() {
      return new WizardTermsPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      userAgreementViewGroup.setLayoutTransition(new LayoutTransition());
      toolbar.setNavigationOnClickListener(v -> presenter.onBack());
      termsView.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageFinished(WebView view, String url) {
            if (view.getProgress() == 100) {
               pb.setVisibility(View.GONE);
               agreeBtn.setVisibility(View.VISIBLE);
            }
         }

         @Override
         public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            agreeBtn.setVisibility(GONE);
            pb.setVisibility(GONE);
            view.setVisibility(INVISIBLE);
            showLoadTermsError();
         }

         @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
            agreeBtn.setVisibility(View.GONE);
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

   public void showLoadTermsError() {
      buildErrorDialog((dialog, which) -> termsView.reload());
   }

   @Override
   protected void onDetachedFromWindow() {
      if (errorDialog != null) {
         errorDialog.dismiss();
         errorDialog = null;
      }

      super.onDetachedFromWindow();
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

   private void buildErrorDialog(MaterialDialog.SingleButtonCallback retryAction){
      errorDialog = new MaterialDialog.Builder(getContext()).title(R.string.wallet_error_label)
            .content(R.string.wallet_terms_and_conditions_load_failed)
            .positiveText(R.string.wallet_retry_label)
            .onPositive(retryAction)
            .show();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @OnClick(R.id.wallet_wizard_terms_and_conditions_agree_btn)
   void onAcceptClicked() {
      getPresenter().acceptTermsPressed();
   }

   @Override
   protected boolean hasToolbar() {
      return true;
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
      if(errorDialog != null) errorDialog.dismiss();
   }
}
