package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WizardTermsScreen extends WalletLinearLayout<WizardTermsScreenPresenter.Screen, WizardTermsScreenPresenter, WizardTermsPath> implements WizardTermsScreenPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.termsView) WebView termsView;
   @InjectView(R.id.wallet_wizard_terms_and_conditions_agree_btn) Button agreeBtn;
   @InjectView(R.id.pb) View pb;

   private Snackbar snackbar;

   public WizardTermsScreen(Context context) {
      super(context);
   }

   public WizardTermsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public WizardTermsScreenPresenter createPresenter() {
      return new WizardTermsScreenPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();

      supportConnectionStatusLabel(false);
      agreeBtn.setVisibility(GONE);
      pb.setVisibility(GONE);

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
            pb.setVisibility(VISIBLE);
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
      showErrorSnackbar(it -> termsView.reload());
   }

   @Override
   protected void onDetachedFromWindow() {
      if (snackbar != null) {
         snackbar.dismiss();
         snackbar = null;
      }

      super.onDetachedFromWindow();
   }

   @Override
   public void showTerms(String url) {
      termsView.loadUrl("about:blank");
      termsView.loadUrl(url);
   }

   @Override
   public void failedToLoadTerms() {
      showErrorSnackbar(it -> getPresenter().loadTerms());
   }

   private void showErrorSnackbar(View.OnClickListener retryAction){
      snackbar = Snackbar.make(termsView, R.string.wallet_terms_and_conditions_load_failed, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.wallet_terms_and_conditions_try_again, it -> {
               if (snackbar != null) retryAction.onClick(it);
            });
      snackbar.show();
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
}
