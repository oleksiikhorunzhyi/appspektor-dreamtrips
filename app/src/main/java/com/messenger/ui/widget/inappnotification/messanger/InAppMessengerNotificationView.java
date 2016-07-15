package com.messenger.ui.widget.inappnotification.messanger;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.notification.model.NotificationData;
import com.messenger.ui.widget.inappnotification.BaseInAppNotificationView;
import com.messenger.util.MessageVersionHelper;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

public abstract class InAppMessengerNotificationView extends BaseInAppNotificationView {
    @InjectView(R.id.in_app_notif_title)
    TextView tvTitle;
    @InjectView(R.id.in_app_notif_text)
    TextView tvText;

    public InAppMessengerNotificationView(Context context) {
        super(context);
    }

    public InAppMessengerNotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InAppMessengerNotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void bindNotification(NotificationData notification) {
        setText(createNotificationText(notification.getSender(), notification.getMessage(), notification.getAttachment()));
    }

    protected void setTitle(String title) {
        tvTitle.setText(title);
    }

    protected void setText(String text) {
        tvText.setText(text);
    }

    protected String createNotificationText(DataUser sender, DataMessage message, @Nullable DataAttachment attachment) {
        if (attachment == null) {
            return getMessageText(sender, message, null);
        }
        switch (attachment.getType()) {
            case AttachmentType.IMAGE:
                return getImagePostMessage(sender);
            case AttachmentType.LOCATION:
                return getLocationPostMessage(sender);
            default:
                return getMessageText(sender, message, attachment.getType());
        }
    }

    protected String getImagePostMessage(DataUser user) {
        return user.getName() + " " + getResources().getString(R.string.sent_photo);
    }

    protected String getLocationPostMessage(DataUser user) {
        return user.getName() + " " + getResources().getString(R.string.sent_location);
    }

    protected String getMessageText(DataUser sender, DataMessage dataMessage, @Nullable String attachmentType) {
        return MessageVersionHelper.isUnsupported(attachmentType) ?
                Html.fromHtml(getResources().getString(R.string.chat_update_proposition)).toString() :
                dataMessage.getText();
    }
}
