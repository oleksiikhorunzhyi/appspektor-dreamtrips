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
import com.worldventures.dreamtrips.core.utils.Share;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.presenter.EditTemplatePresenter;

import java.util.List;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preview:
                getPresenter().updatePreview();
                break;
            case R.id.action_send:
                List<String> membersAddress = getPresenter().getMembersAddress();
                String[] addresses = membersAddress.toArray(new String[membersAddress.size()]);

                Intent intent = Share.newEmailIntent(
                        addresses,
                        getPresenter().getSubject(),
                        getPresenter().getBody()
                );
                Intent smsIntent = Share.newSmsIntent(addresses, getPresenter().getSmsBody());
                startActivity(Intent.createChooser(smsIntent, "Share"));
                break;
        }
        return super.onOptionsItemSelected(item);
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

    }

    @Override
    public void finishLoading() {

    }
}
