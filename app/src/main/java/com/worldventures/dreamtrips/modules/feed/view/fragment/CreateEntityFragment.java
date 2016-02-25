package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.net.Uri;
import android.view.View;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.feed.bundle.CreatePostBundle;
import com.worldventures.dreamtrips.modules.feed.presenter.CreateEntityPresenter;

import butterknife.OnClick;

public abstract class CreateEntityFragment<PM extends CreateEntityPresenter> extends ActionEntityFragment<PM, CreatePostBundle>
        implements CreateEntityPresenter.View {

    @Override
    protected int getPostButtonText() {
        return R.string.post;
    }

    @Override
    public void attachPhoto(Uri uri) {
        if (uri != null) {
            attachedPhoto.setController(GraphicUtils.provideFrescoResizingController(uri, attachedPhoto.getController()));
            post.setHint(R.string.photo_hint);
            imageContainer.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.ic_post_add_image_selected);
        } else {
            attachedPhoto.setImageURI(null);
            post.setHint(R.string.post_hint);
            imageContainer.setVisibility(View.GONE);
            image.setImageResource(R.drawable.ic_post_add_image_normal);
        }
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
