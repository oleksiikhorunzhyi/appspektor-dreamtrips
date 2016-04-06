package com.messenger.ui.adapter.holder.chat;

import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.messenger.messengerservers.constant.MessageStatus;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;

import butterknife.InjectView;

public abstract class ImageMessageViewHolder extends MessageHolder {

    protected static final float ALPHA_IMAGE_POST_SENDING = 0.5f;
    protected static final float ALPHA_IMAGE_POST_NORMAL = 1f;

    @InjectView(R.id.chat_image_post_image_view)
    SimpleDraweeView imagePostView;
    @InjectView(R.id.chat_image_post_progress_bar)
    ProgressBar progressBar;
    @InjectView(R.id.chat_image_error)
    View errorView;

    protected Uri imagePostUri;

    public ImageMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void updateMessageStatusUi(boolean needMarkUnreadMessage) {
        switch (message.getStatus()) {
            case MessageStatus.ERROR:
                applyErrorStatusUi();
                break;
            case MessageStatus.SENT:
            case MessageStatus.READ:
                errorView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                imagePostView.setAlpha(ALPHA_IMAGE_POST_NORMAL);
                break;
        }
    }

    protected void applyErrorStatusUi() {
        errorView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    protected void applyLoadingStatusUi() {
        errorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
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

        int width = itemView.getResources().getDimensionPixelSize(R.dimen.chat_image_width);
        int height = itemView.getResources().getDimensionPixelSize(R.dimen.chat_image_height);

        DraweeController controller = GraphicUtils.provideFrescoResizingControllerBuilder(uri,
                imagePostView.getController(), width, height)
                .setControllerListener(getLoadingListener())
                .build();

        imagePostView.setController(controller);
    }

    public void setOnImageClickListener(View.OnClickListener clickListener) {
        imagePostView.setOnClickListener(clickListener);
    }

    @Override
    public View getMessageView() {
        return imagePostView;
    }

    protected abstract BaseControllerListener<ImageInfo> getLoadingListener();
}
