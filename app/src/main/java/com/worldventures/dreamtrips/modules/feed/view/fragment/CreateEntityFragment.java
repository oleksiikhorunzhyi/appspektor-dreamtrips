package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;

import butterknife.OnClick;

public abstract class CreateEntityFragment<PM extends CreateEntityPresenter> extends ActionEntityFragment<PM, CreateEntityBundle>
        implements CreateEntityPresenter.View {

    @Override
    protected int getPostButtonText() {
        return R.string.post;
    }

    @Override
    public void showProgress() {
        shadow.setVisibility(View.VISIBLE);
        fabProgress.setVisibility(View.VISIBLE);
        fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
        fabProgress.setIndeterminate(true);
        fabProgress.showProgress(true);
        int color = getResources().getColor(R.color.bucket_blue);
        circleView.setColor(color);
    }

    @Override
    public void hideProgress() {
        fabProgress.setVisibility(View.GONE);
        shadow.setVisibility(View.GONE);
    }

    @Override
    public void imageError() {
        fabProgress.showProgress(false);
        fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
        int color = getResources().getColor(R.color.bucket_red);
        circleView.setColor(color);
    }

    @OnClick(R.id.cancel_action)
    void onPhotoCancel() {
        getPresenter().cancelClicked();
    }

    @OnClick(R.id.fab_progress)
    void onProgressClick() {
        getPresenter().onProgressClicked();
    }
}
