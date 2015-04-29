package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
