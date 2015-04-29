package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.presenter.EditTemplatePresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_edit_template)
@MenuResource(R.menu.menu_edit_template)
public class EditTemplateFragment extends BaseFragment<EditTemplatePresenter> implements EditTemplatePresenter.View {
    public static final String TEMPLATE = "TEMPLATE";

    @InjectView(R.id.tv_from)
    TextView tvFrom;
    @InjectView(R.id.tv_to)
    TextView tvTo;
    @InjectView(R.id.tv_subj)
    TextView tvSubj;
    @InjectView(R.id.wv_preview)
    WebView wvPreview;
    @InjectView(R.id.et_email)
    MaterialEditText etMessage;
    @InjectView(R.id.ll_progress)
    View progressView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preview:
                getPresenter().updatePreview();
                break;
            case R.id.action_send:
                shareAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareAction() {
        Intent smsIntent = getPresenter().getShareIntent();
        startActivity(Intent.createChooser(smsIntent, "Share"));
        getPresenter().notifyServer();
    }


    @Override
    protected EditTemplatePresenter createPresenter(Bundle savedInstanceState) {
        InviteTemplate template = (InviteTemplate) getArguments().getSerializable(TEMPLATE);
        return new EditTemplatePresenter(this, template);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        wvPreview.getSettings().setLoadWithOverviewMode(true);
        wvPreview.getSettings().setUseWideViewPort(true);
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void setFrom(String from) {
        tvFrom.setText(from);
    }

    @Override
    public void setSubject(String title) {
        tvSubj.setText(title);
    }

    @Override
    public void setTo(String s) {
        tvTo.setText(s);
    }

    @Override
    public void setWebViewContent(String content) {
        wvPreview.loadData(content, "text/html", "UTF-8");
    }

    @Override
    public String getMessage() {
        return etMessage.getText().toString();
    }

    @Override
    public void startLoading() {
        progressView.setVisibility(View.VISIBLE);

    }

    @Override
    public void finishLoading() {
        progressView.setVisibility(View.GONE);
    }
}
