package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.HeaderProvider;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.modules.common.presenter.TermsConditionsDialogPresenter;

import java.util.Collections;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.dialog_terms_conditions)
public class TermsConditionsDialog extends BaseDialogFragmentWithPresenter<TermsConditionsDialogPresenter> implements TermsConditionsDialogPresenter.View {

   @InjectView(R.id.terms_content) WebView termsContent;
   @InjectView(R.id.accept_checkbox) CheckBox acceptCheckbox;
   @InjectView(R.id.accept) Button btnAccept;
   @InjectView(R.id.reject) Button btnReject;
   @InjectView(R.id.btn_retry) ImageButton btnRetry;

   @Inject HeaderProvider headerProvider;

   private boolean onErrorReceived;

   // After actual request for Terms and Conditions and onPageFinished called, we get onReceivedHttpError for other url
   // we cannot match urls to ensure that wrong one was failed, cause we are using redirects
   // so this is the only decision
   private boolean onPageShown;

   public static TermsConditionsDialog create() {
      return new TermsConditionsDialog();
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      termsContent.getSettings().setJavaScriptEnabled(true);
      termsContent.setWebViewClient(new WebViewClient() {
         @Override
         public void onPageFinished(WebView view, String url) {
            if (termsContent == null || btnRetry == null || onErrorReceived) {
               return;
            }
            onPageShown = true;
            termsContent.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.GONE);
         }

         @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            onErrorReceived = false;
            onPageShown = false;
            if (termsContent != null && btnRetry != null) {
               termsContent.setVisibility(View.GONE);
               btnRetry.setVisibility(View.GONE);
            }
         }

         @Override
         public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (onPageShown) {
               return;
            }
            onErrorReceived = true;
            showRetryButton();
         }

         @Override
         public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(MailTo.MAILTO_SCHEME) && getActivity() != null) {
               String mailTo = url.substring(MailTo.MAILTO_SCHEME.length());
               getActivity().startActivity(IntentUtils.newEmailIntent("", "", Collections.singletonList(mailTo)));
               return true;
            }
            return false;
         }
      });

      acceptCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
         if (!onPageShown || onErrorReceived) {
            acceptCheckbox.setChecked(false);
            return;
         }
         btnAccept.setEnabled(isChecked);
      });

      initAcceptRejectButtons();
   }

   private void initAcceptRejectButtons() {
      btnAccept.setOnClickListener(v -> presenter.acceptTerms());
      btnReject.setOnClickListener(v -> presenter.denyTerms());
      btnRetry.setOnClickListener(v -> presenter.retry());
      setEqualAcceptRejectButtonsHeight();
   }

   private void setEqualAcceptRejectButtonsHeight() {
      btnAccept.setEnabled(true);
      final ViewTreeObserver.OnPreDrawListener globalLayoutListener = new ViewTreeObserver.OnPreDrawListener() {
         @Override
         public boolean onPreDraw() {
            int maxHeight = Math.max(btnAccept.getMeasuredHeight(), btnReject.getMeasuredHeight());
            btnAccept.getLayoutParams().height = maxHeight;
            btnReject.getLayoutParams().height = maxHeight;
            btnAccept.getViewTreeObserver().removeOnPreDrawListener(this);
            btnAccept.setEnabled(false);
            return true;
         }
      };
      btnAccept.getViewTreeObserver().addOnPreDrawListener(globalLayoutListener);
   }

   @Override
   public void onDestroyView() {
      termsContent.stopLoading();
      super.onDestroyView();
   }

   @NonNull
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      setStyle(DialogFragment.STYLE_NO_TITLE, 0);
      setCancelable(false);
      return super.onCreateDialog(savedInstanceState);
   }

   @Override
   protected TermsConditionsDialogPresenter createPresenter() {
      return new TermsConditionsDialogPresenter();
   }

   @Override
   public void loadContent(String url) {
      termsContent.loadUrl(url, headerProvider.getStandardWebViewHeaders());
   }

   @Override
   public void dismissDialog() {
      this.dismissIfShown(getFragmentManager());
   }

   @Override
   public void enableButtons() {
      btnAccept.setEnabled(acceptCheckbox.isChecked());
      btnReject.setEnabled(true);
   }

   @Override
   public void disableButtons() {
      btnAccept.setEnabled(false);
      btnReject.setEnabled(false);
   }

   @Override
   public void showRetryButton() {
      if (termsContent != null && btnRetry != null) {
         termsContent.setVisibility(View.GONE);
         btnRetry.setVisibility(View.VISIBLE);
         btnAccept.setEnabled(false);
      }
   }
}
