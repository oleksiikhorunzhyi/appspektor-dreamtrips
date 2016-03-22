package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.app.Dialog;
import android.net.MailTo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.IntentUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.TermsConditionsDialogPresenter;

import butterknife.InjectView;

@Layout(R.layout.dialog_terms_conditions)
public class TermsConditionsDialog extends BaseDialogFragmentWithPresenter<TermsConditionsDialogPresenter> implements TermsConditionsDialogPresenter.View {

    @InjectView(R.id.terms_content)
    WebView termsContent;
    @InjectView(R.id.accept_checkbox)
    CheckBox acceptCheckbox;
    @InjectView(R.id.accept)
    Button accept;
    @InjectView(R.id.reject)
    Button reject;

    private String termsText;

    public static TermsConditionsDialog create() {
        return new TermsConditionsDialog();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        termsContent.getSettings().setJavaScriptEnabled(true);
        termsContent.addJavascriptInterface(new ContentJavaScriptInterface(), "HtmlViewer");
        termsContent.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (termsContent == null) return;

                termsContent.loadUrl("javascript:window.HtmlViewer.getHtml" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(MailTo.MAILTO_SCHEME) && getActivity() != null) {
                    String mailTo = url.substring(MailTo.MAILTO_SCHEME.length());
                    getActivity().startActivity(IntentUtils.newEmailIntent("", "", mailTo));
                    return true;
                }
                return false;
            }
        });

        acceptCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (termsText == null && isChecked) {
                acceptCheckbox.setChecked(false);

                return;
            }

            accept.setEnabled(isChecked);
        });
        accept.setOnClickListener(v -> {
            presenter.acceptTerms(termsText);
        });
        reject.setOnClickListener(v -> {
            presenter.denyTerms();
        });
    }

    class ContentJavaScriptInterface {

        @JavascriptInterface
        public void getHtml(String html) {
            termsText = html;
        }
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
        termsContent.loadUrl(url);
    }

    @Override
    public void dismissDialog() {
        this.dismissIfShown(getFragmentManager());
    }

    @Override
    public void enableButtons() {
        accept.setEnabled(acceptCheckbox.isChecked());
        reject.setEnabled(true);
    }

    @Override
    public void disableButtons() {
        accept.setEnabled(false);
        reject.setEnabled(false);
    }
}
