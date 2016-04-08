package com.messenger.ui.adapter.holder.chat;

import android.graphics.drawable.Animatable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.messenger.util.Utils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;
import butterknife.OnClick;

public class OwnImageMessageViewHolder extends ImageMessageViewHolder implements MessageHolder.OwnMessageHolder {

    @InjectView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;

    private MessagesCursorAdapter.OnRepeatMessageSend onRepeatMessageSendListener;

    public OwnImageMessageViewHolder(View itemView) {
        super(itemView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imagePostView.getLayoutParams();
        params.setMargins(freeSpaceForMessageRowOwnMessage, params.topMargin, params.rightMargin,
                params.bottomMargin);
        errorView.setOnClickListener(view -> {
            reloadImage();
            if (message.getStatus() == MessageStatus.ERROR) {
                if (onRepeatMessageSendListener != null) {
                    onRepeatMessageSendListener.onRepeatMessageSend(message);
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // General message logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setBubbleBackground() {
        int backgroundResource;
        if (isPreviousMessageFromTheSameUser) {
            itemView.setPadding(itemView.getPaddingLeft(), 0, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected ? R.drawable.dark_blue_bubble_image_post : R.drawable.blue_bubble_image_post;
        } else {
            itemView.setPadding(itemView.getPaddingLeft(), rowVerticalMargin, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected ? R.drawable.dark_blue_bubble_comics_image_post : R.drawable.blue_bubble_comics_image_post;
        }
        imagePostView.setBackgroundResource(backgroundResource);
    }

    @Override
    public void updateMessageStatusUi(boolean needMarkUnreadMessage) {
        super.updateMessageStatusUi(needMarkUnreadMessage);
        if (message.getStatus() == MessageStatus.SENDING) {
            applyLoadingStatusUi();
            imagePostView.setAlpha(ALPHA_IMAGE_POST_SENDING);
        }
        boolean visible = message.getStatus() == MessageStatus.ERROR;
        int viewVisible = visible ? View.VISIBLE : View.GONE;
        if (viewVisible != viewSwitcher.getVisibility()) {
            viewSwitcher.setVisibility(viewVisible);
        }
        if (visible && viewSwitcher.getCurrentView().getId() == R.id.progress_bar) {
            viewSwitcher.showPrevious();
        }
        chatMessageContainer.setBackgroundResource(R.color.chat_list_item_read_read_background);
    }

    @OnClick(R.id.iv_message_error)
    void onMessageErrorClicked() {
        if (onRepeatMessageSendListener != null) {
            onRepeatMessageSendListener.onRepeatMessageSend(message);
            viewSwitcher.showNext();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Image message logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected BaseControllerListener<ImageInfo> getLoadingListener() {
        return new BaseControllerListener<ImageInfo>() {
            @Override
            public void onSubmit(String id, Object callerContext) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                if (message.getStatus() != MessageStatus.SENDING) {
                    progressBar.setVisibility(View.GONE);
                }

                //TODO should be refactored
                if (message.getStatus() == MessageStatus.ERROR &&
                        Utils.isFileUri(imagePostUri)) {
                    errorView.setVisibility(View.VISIBLE);
                    viewSwitcher.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                applyErrorStatusUi();
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////
    // Own message logic
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setOnRepeatMessageListener(MessagesCursorAdapter.OnRepeatMessageSend listener) {
        this.onRepeatMessageSendListener = listener;
    }
}