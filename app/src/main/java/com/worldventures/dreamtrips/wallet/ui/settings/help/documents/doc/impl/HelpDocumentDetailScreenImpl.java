package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.impl;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.HelpDocumentDetailPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.HelpDocumentDetailScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class HelpDocumentDetailScreenImpl extends WalletBaseController<HelpDocumentDetailScreen, HelpDocumentDetailPresenter> implements HelpDocumentDetailScreen {

   private static final String KEY_HELP_DOCUMENT = "key_help_document";

   private Toolbar toolbar;
   private WebView webView;
   private View pb;

   @Inject HelpDocumentDetailPresenter presenter;

   public static HelpDocumentDetailScreenImpl create(WalletDocumentModel document) {
      final Bundle args = new Bundle();
      args.putParcelable(KEY_HELP_DOCUMENT, document);
      return new HelpDocumentDetailScreenImpl(args);
   }

   public HelpDocumentDetailScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      setUpView(view);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_help_document, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }

   private void setUpView(View view) {
      toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      pb = view.findViewById(R.id.pb);
      webView = view.findViewById(R.id.document_view);
      webView.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageFinished(WebView view, String url) {
            if (view.getProgress() == 100) {
               pb.setVisibility(GONE);
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
               getPresenter().sendEmail(Uri.parse(url), getString(R.string.wallet_email_app_choose_dialog_title));
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
      getPresenter().goBack();
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
   public WalletDocumentModel getDocument() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_HELP_DOCUMENT))
            ? getArgs().getParcelable(KEY_HELP_DOCUMENT)
            : null;
   }

   @Override
   public void showDocument(WalletDocumentModel document) {
      toolbar.setTitle(document.getName());
      webView.loadUrl(document.getUrl());
   }

   @Override
   public HelpDocumentDetailPresenter getPresenter() {
      return presenter;
   }
}