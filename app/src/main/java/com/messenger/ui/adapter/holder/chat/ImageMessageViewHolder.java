package com.messenger.ui.adapter.holder.chat;

import android.database.Cursor;
import android.graphics.drawable.Animatable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.messenger.entities.DataPhotoAttachment;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.storage.dao.MessageDAO;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;

import butterknife.InjectView;
import butterknife.OnClick;

public abstract class ImageMessageViewHolder extends MessageViewHolder {

    @InjectView(R.id.chat_image_post_image_view)
    SimpleDraweeView imagePostView;
    @InjectView(R.id.chat_image_post_progress_bar)
    ProgressBar progressBar;
    @InjectView(R.id.chat_image_error)
    View errorView;

    private String attachmentId;

    protected DataPhotoAttachment dataPhotoAttachment;

    public ImageMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindCursor(Cursor cursor) {
        super.bindCursor(cursor);
        dataPhotoAttachment = SqlUtils.convertToModel(true, DataPhotoAttachment.class, cursor);
        attachmentId = cursor.getString(cursor.getColumnIndex(MessageDAO.ATTACHMENT_ID));
        loadImage();
        updateMessageStatusUi();
    }

    @OnClick(R.id.chat_image_post_image_view)
    void onImageClicked() {
        cellDelegate.onImageClicked(attachmentId);
    }

    public void updateMessageStatusUi() {
        switch (dataMessage.getStatus()) {
            case MessageStatus.ERROR:
                applyErrorStatusUi();
                break;
            case MessageStatus.SENT:
            case MessageStatus.READ:
                errorView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                imagePostView.setAlpha(ALPHA_MESSAGE_NORMAL);
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

    public void loadImage() {
        showImageMessage(dataPhotoAttachment.getUrl(), dataPhotoAttachment.getLocalUri());
    }

    private void showImageMessage(@Nullable String strUri, @Nullable String strLocalUri) {
        applyLoadingStatusUi();

        int width = itemView.getResources().getDimensionPixelSize(R.dimen.chat_image_width);
        int height = itemView.getResources().getDimensionPixelSize(R.dimen.chat_image_height);

        DraweeController controller = GraphicUtils
                .provideFrescoResizingControllerBuilder(strUri, strLocalUri, imagePostView.getController(), width, height)
                .setControllerListener(controllerListener)
                .build();

        imagePostView.setController(controller);
    }

    protected void onImageDisplayed() {
        progressBar.setVisibility(View.GONE);
    }

    protected void onStartLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private BaseControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onSubmit(String id, Object callerContext) {
            onStartLoading();
        }

        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            onImageDisplayed();
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            applyErrorStatusUi();
        }
    };
}
