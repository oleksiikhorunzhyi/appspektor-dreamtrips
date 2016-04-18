package com.messenger.ui.adapter.holder.chat;

import android.view.View;

import com.messenger.entities.DataPhotoAttachment;
import com.messenger.messengerservers.constant.MessageStatus;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;

import butterknife.OnClick;

@Layout(R.layout.list_item_chat_own_image_message)
public class OwnImageMessageViewHolder extends ImageMessageViewHolder {

    public OwnImageMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void updateMessageStatusUi() {
        super.updateMessageStatusUi();
        if (dataMessage.getStatus() == MessageStatus.SENDING) {
            applyLoadingStatusUi();
            imagePostView.setAlpha(ALPHA_MESSAGE_SENDING);
        }

        boolean isError = dataMessage.getStatus() == MessageStatus.ERROR;
        int viewVisible = isError ? View.VISIBLE : View.GONE;
        if (viewVisible != retrySwitcher.getVisibility()) {
            retrySwitcher.setVisibility(viewVisible);
        }
        if (isError && retrySwitcher.getCurrentView().getId() == R.id.progress_bar) {
            retrySwitcher.showPrevious();
        }
    }

    @OnClick(R.id.chat_image_error)
    void onMessageErrorClicked() {
        if (dataMessage.getStatus() == MessageStatus.ERROR) {
            cellDelegate.onRetryClicked(dataMessage);
        } else loadImage();
    }

    @Override
    protected void onImageDisplayed() {
        if (dataMessage.getStatus() != MessageStatus.SENDING) {
            progressBar.setVisibility(View.GONE);
        }

        //TODO should be refactored
        if (dataMessage.getStatus() == MessageStatus.ERROR &&
                    dataPhotoAttachment.getUploadState() == DataPhotoAttachment.PhotoAttachmentStatus.UPLOADED) {
            errorView.setVisibility(View.VISIBLE);
            retrySwitcher.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStartLoading() {
        if (dataPhotoAttachment.getLocalPath() == null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}