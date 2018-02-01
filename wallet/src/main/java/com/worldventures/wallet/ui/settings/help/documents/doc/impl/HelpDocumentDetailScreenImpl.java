package com.worldventures.wallet.ui.settings.help.documents.doc.impl;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.common.base.WalletBaseController;
import com.worldventures.wallet.ui.settings.help.documents.doc.HelpDocumentDetailPresenter;
import com.worldventures.wallet.ui.settings.help.documents.doc.HelpDocumentDetailScreen;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletDocumentModel;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class HelpDocumentDetailScreenImpl extends WalletBaseController<HelpDocumentDetailScreen, HelpDocumentDetailPresenter> implements HelpDocumentDetailScreen {

   private static final String KEY_STATE_CONTENT_LOADED = "HelpDocumentDetailScreenImpl#KEY_STATE_CONTENT_LOADED";
   private static final String KEY_STATE_USE_WIDE_VIEW_PORT = "HelpDocumentDetailScreenImpl#KEY_STATE_USE_WIDE_VIEW_PORT";
   private static final String KEY_STATE_LOAD_WITH_OVERVIEW_MODE = "HelpDocumentDetailScreenImpl#KEY_STATE_LOAD_WITH_OVERVIEW_MODE";
   private static final String KEY_STATE_TITLE = "HelpDocumentDetailScreenImpl#KEY_STATE_TITLE";

   private static final String KEY_HELP_DOCUMENT = "key_help_document";
   private static final String USER_GUIDE_NAME = "flye_user_guide";

   private Toolbar toolbar;
   private WebView webView;
   private View progressView;

   @Inject HelpDocumentDetailPresenter presenter;

   private boolean contentLoaded;

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
      progressView = view.findViewById(R.id.pb);
      webView = view.findViewById(R.id.document_view);
      webView.getSettings().setBuiltInZoomControls(true);
      webView.getSettings().setDisplayZoomControls(false);
      webView.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageFinished(WebView view, String url) {
            if (view.getProgress() == 100) {
               progressView.setVisibility(GONE);
            }
         }

         @Override
         public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            progressView.setVisibility(GONE);
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

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      if (!contentLoaded) {
         presenter.fetchDocument();
      }
   }

   private void showLoadDocError() {
      buildErrorDialog((dialog, which) -> webView.reload());
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   private void buildErrorDialog(MaterialDialog.SingleButtonCallback retryAction) {
      new MaterialDialog.Builder(getContext()).title(R.string.wallet_error_label)
            .content(R.string.wallet_settings_help_documents_load_failed)
            .positiveText(R.string.wallet_retry_label)
            .onPositive(retryAction)
            .cancelable(false)
            .show();
   }

   @Override
   public WalletDocumentModel getDocument() {
      return !getArgs().isEmpty() && getArgs().containsKey(KEY_HELP_DOCUMENT)
            ? getArgs().getParcelable(KEY_HELP_DOCUMENT)
            : null;
   }

   @Override
   public void showDocument(WalletDocumentModel document) {
      contentLoaded = true;
      if (document.getUrl().contains(USER_GUIDE_NAME)) { // adjust only user guide documents // workaround for one doc
         webView.getSettings().setLoadWithOverviewMode(true);
         webView.getSettings().setUseWideViewPort(true);
      }
      toolbar.setTitle(document.getName());
      webView.loadUrl(document.getUrl());
   }

   @Override
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      super.onSaveViewState(view, outState);
      outState.putBoolean(KEY_STATE_CONTENT_LOADED, contentLoaded);
      outState.putCharSequence(KEY_STATE_TITLE, toolbar.getTitle());
      outState.putBoolean(KEY_STATE_LOAD_WITH_OVERVIEW_MODE, webView.getSettings().getLoadWithOverviewMode());
      outState.putBoolean(KEY_STATE_USE_WIDE_VIEW_PORT, webView.getSettings().getUseWideViewPort());
      webView.saveState(outState);
   }

   @Override
   protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
      super.onRestoreViewState(view, savedViewState);
      webView.restoreState(savedViewState);
      webView.getSettings().setLoadWithOverviewMode(savedViewState.getBoolean(KEY_STATE_LOAD_WITH_OVERVIEW_MODE));
      webView.getSettings().setUseWideViewPort(savedViewState.getBoolean(KEY_STATE_USE_WIDE_VIEW_PORT));
      toolbar.setTitle(savedViewState.getCharSequence(KEY_STATE_TITLE));
      contentLoaded = savedViewState.getBoolean(KEY_STATE_CONTENT_LOADED, false);
   }

   @Override
   public HelpDocumentDetailPresenter getPresenter() {
      return presenter;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new HelpDocumentDetailScreenModule();
   }
}
