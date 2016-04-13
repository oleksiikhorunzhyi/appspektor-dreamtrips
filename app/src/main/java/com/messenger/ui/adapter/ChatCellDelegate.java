package com.messenger.ui.adapter;

import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;

public interface ChatCellDelegate {

    void onAvatarClicked(DataUser dataUser);

    void onImageClicked(String attachmentId);

    void onMessageLongClicked(DataMessage dataMessage);

    void onRetryClicked(DataMessage dataMessage);
}
