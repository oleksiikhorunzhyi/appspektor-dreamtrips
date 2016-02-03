package com.messenger.ui.adapter.holder;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.messenger.messengerservers.constant.MessageStatus;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import timber.log.Timber;

public abstract class ImageMessageViewHolder extends MessageHolder {

    protected static final float ALPHA_IMAGE_POST_SENDING = 0.5f;
    protected static final float ALPHA_IMAGE_POST_NORMAL = 1f;

    @InjectView(R.id.chat_image_post_image_view)
    SimpleDraweeView imagePostView;
    @InjectView(R.id.chat_image_post_progress_bar)
    ProgressBar progressBar;
    @InjectView(R.id.chat_image_error)
    View errorView;

    private Uri imagePostUri;

    public ImageMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void updateMessageStatusUi() {
        switch (message.getStatus()) {
            case MessageStatus.ERROR:
                applyErrorStatusUi();
                break;
            case MessageStatus.SENT:
            case MessageStatus.READ:
                errorView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                imagePostView.setVisibility(View.VISIBLE);
                imagePostView.setAlpha(ALPHA_IMAGE_POST_NORMAL);
                break;
        }
    }

    protected void applyErrorStatusUi() {
        errorView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        imagePostView.setVisibility(View.GONE);
    }

    protected void applyLoadingStatusUi() {
        errorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        imagePostView.setVisibility(View.VISIBLE);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Image message logic
    ///////////////////////////////////////////////////////////////////////////

    public void reloadImage() {
        showImageMessage(imagePostUri);
    }

    public void showImageMessage(Uri uri) {
        this.imagePostUri = uri;

        applyLoadingStatusUi();

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithResourceId(imagePostView.getId())
                .setSource(uri)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setControllerListener(getLoadingListener())
                .setOldController(imagePostView.getController())
                .build();
        imagePostView.setController(controller);
    }

    @Override
    public View getViewForClickableTimestamp() {
        return imagePostView;
    }

    protected abstract BaseControllerListener<ImageInfo> getLoadingListener();
}
