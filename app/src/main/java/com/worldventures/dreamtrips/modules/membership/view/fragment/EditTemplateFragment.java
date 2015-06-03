package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketPhotosView;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.presenter.EditTemplatePresenter;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_edit_template)
@MenuResource(R.menu.menu_edit_template)
public class EditTemplateFragment extends BaseFragment<EditTemplatePresenter> implements EditTemplatePresenter.View {
    public static final String TEMPLATE = "TEMPLATE";

    public static final String EXTRA_MESSAGE = "message";

    @Inject
    @ForActivity
    Provider<Injector> injector;

    @InjectView(R.id.tv_from)
    TextView tvFrom;
    @InjectView(R.id.tv_to)
    TextView tvTo;
    @InjectView(R.id.tv_subj)
    TextView tvSubj;
    @InjectView(R.id.wv_preview)
    WebView wvPreview;
    @InjectView(R.id.et_personal_message)
    MaterialEditText etMessage;
    @InjectView(R.id.ll_progress)
    View progressView;
    @InjectView(R.id.photoContainer)
    ViewGroup photoContainer;

    @InjectView(R.id.bucket_photos)
    protected BucketPhotosView bucketPhotosView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_preview:
                getPresenter().previewAction();
                break;
            case R.id.action_send:
                getPresenter().shareRequest();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_MESSAGE, etMessage.getText().toString());
    }

    @Override
    protected EditTemplatePresenter createPresenter(Bundle savedInstanceState) {
        InviteTemplate template = getArguments().getParcelable(TEMPLATE);
        return new EditTemplatePresenter(template);
    }

    @Override
    public void hidePhotoUpload() {
        photoContainer.setVisibility(View.GONE);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        wvPreview.getSettings().setJavaScriptEnabled(true);
        wvPreview.getSettings().setLoadWithOverviewMode(true);
        wvPreview.getSettings().setUseWideViewPort(true);
        progressView.setVisibility(View.GONE);

        bucketPhotosView.init(this, injector, BucketPhotosView.Type.DEFAULT);
        bucketPhotosView.setMakePhotoImageCallback(getPresenter().getPhotoChooseCallback());
        bucketPhotosView.setChooseImageCallback(getPresenter().getPhotoChooseCallback());
        bucketPhotosView.setFbImageCallback(getPresenter().getFbCallback());
        bucketPhotosView.setDeleteButtonCallback(getPresenter().getDeleteCallback());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            etMessage.setText(savedInstanceState.getString(EXTRA_MESSAGE));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bucketPhotosView.onActivityResult(requestCode, resultCode, data);
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
        wvPreview.loadData(content, "text/html; charset=UTF-8", null);
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
    public IBucketPhotoView getBucketPhotosView() {
        return bucketPhotosView;
    }

    @Override
    public void finishLoading() {
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        wvPreview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        wvPreview.onPause();
    }

    @Override
    public void onDestroyView() {
        wvPreview.loadUrl("about:blank");
        wvPreview.destroy();
        wvPreview = null;
        super.onDestroyView();
    }
}
