package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;

public class HelpDocumentScreen extends WalletLinearLayout<HelpDocumentPresenter.Screen, HelpDocumentPresenter, HelpDocumentPath>
      implements HelpDocumentPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.document_view) WebView webView;
   @InjectView(R.id.pb) View pb;

   public HelpDocumentScreen(Context context) {
      super(context);
   }

   public HelpDocumentScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public HelpDocumentPresenter createPresenter() {
      return new HelpDocumentPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      if (isInEditMode()) return;
      setUpView();
   }

   private void setUpView() {
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      toolbar.setTitle(getPath().getModel().getName());
      webView.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageFinished(WebView view, String url) {
            if (view.getProgress() == 100) {
               pb.setVisibility(View.GONE);
            }
         }

         @Override
         public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            pb.setVisibility(GONE);
            view.setVisibility(INVISIBLE);
            showLoadDocError();
         }

         @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.setVisibility(VISIBLE);
         }

         @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:")) {
               Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
               getContext().startActivity(Intent.createChooser(emailIntent, getString(R.string.wallet_email_app_choose_dialog_title)));
               view.reload();
               return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
         }
      });
   }

   private void showLoadDocError() {
      buildErrorDialog((dialog, which) -> webView.reload());
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   private void buildErrorDialog(MaterialDialog.SingleButtonCallback retryAction) {
      new MaterialDialog.Builder(getContext()).title(R.string.wallet_error_label)
            .content(R.string.wallet_terms_and_conditions_load_failed)
            .positiveText(R.string.wallet_retry_label)
            .onPositive(retryAction)
            .cancelable(false)
            .show();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void showDocument() {
      webView.loadUrl(getPath().getModel().getUrl());
   }
}
