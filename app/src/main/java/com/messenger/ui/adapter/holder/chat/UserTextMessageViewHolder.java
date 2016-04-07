package com.messenger.ui.adapter.holder.chat;

import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataTranslation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.messenger.util.TruncateUtils;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;
import static com.messenger.messengerservers.constant.TranslationStatus.ERROR;
import static com.messenger.messengerservers.constant.TranslationStatus.REVERTED;
import static com.messenger.messengerservers.constant.TranslationStatus.TRANSLATED;
import static com.messenger.messengerservers.constant.TranslationStatus.TRANSLATING;


public class UserTextMessageViewHolder extends TextMessageViewHolder implements MessageHolder.UserMessageHolder {

    @InjectView(R.id.chat_item_avatar)
    ImageView avatarImageView;
    @InjectView(R.id.chat_username)
    TextView nameTextView;
    @InjectView(R.id.message_container)
    FrameLayout messageContainer;
    @InjectView(R.id.translation_progress)
    ProgressBar translationProgress;
    @InjectView(R.id.translation_status)
    TextView translationStatus;
    @InjectView(R.id.message_linear_layout)
    LinearLayout messageLinearLayout;
//    @InjectView(R.id.ic_translation_available)
//    ImageView iconTranslation;

    private DataTranslation translation;
    private String userLocale;

    public UserTextMessageViewHolder(View itemView) {
        super(itemView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getMessageView().getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, freeSpaceForMessageRowUserMessage,
                params.bottomMargin);
    }

    ///////////////////////////////////////////////////////////////////////////
    // General messages logic
    ///////////////////////////////////////////////////////////////////////////


    public void setTranslation(DataTranslation translation, String userLocale) {
        this.translation = translation;
        this.userLocale = userLocale;
    }

    @Override
    public void showMessage() {
        messageTextView.setAutoLinkMask(Linkify.WEB_URLS);
        applyTranslationStatus();
    }

    @Override
    public void setBubbleBackground() {
        int backgroundResource;
        if (isPreviousMessageFromTheSameUser) {
            itemView.setPadding(itemView.getPaddingLeft(), 0, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected ? R.drawable.dark_grey_bubble
                    : R.drawable.grey_bubble;
        } else {
            itemView.setPadding(itemView.getPaddingLeft(), rowVerticalMargin, itemView.getPaddingRight(), itemView.getPaddingBottom());
            backgroundResource = isSelected ? R.drawable.dark_grey_bubble_comics
                    : R.drawable.grey_bubble_comics;
        }

        messageContainer.setBackgroundResource(backgroundResource);
    }

    @Override
    public void updateMessageStatusUi(boolean needMarkUnreadMessage) {
        if (message.getStatus() == MessageStatus.SENT && needMarkUnreadMessage) {
            chatMessageContainer.setBackgroundResource(R.color.chat_list_item_read_unread_background);
        } else {
            chatMessageContainer.setBackgroundResource(R.color.chat_list_item_read_read_background);
        }
    }

    @Override
    public View getMessageView() {
        return messageContainer;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Someones message logic, delegate execution to SomeoneMessageHolderDelegate
    ///////////////////////////////////////////////////////////////////////////

    private UserMessageHolderDelegate someoneMessageHolderHelper =
            new UserMessageHolderDelegate(avatarImageView, nameTextView);

    @Override
    public void setAuthor(DataUser user) {
        someoneMessageHolderHelper.setAuthor(user);
    }

    @Override
    public void setAvatarClickListener(MessagesCursorAdapter.OnAvatarClickListener listener) {
        someoneMessageHolderHelper.setAvatarClickListener(listener);
    }

    @Override
    public void setPreviousMessageFromTheSameUser(boolean previousMessageFromTheSameUser) {
        super.setPreviousMessageFromTheSameUser(previousMessageFromTheSameUser);
        someoneMessageHolderHelper.setIsPreviousMessageFromTheSameUser(previousMessageFromTheSameUser);
    }

    @Override
    public void updateAvatar() {
        someoneMessageHolderHelper.updateAvatar();
    }

    @Override
    public void updateName(DataConversation dataConversation) {
        someoneMessageHolderHelper.updateName(dataConversation);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Translation staff
    ///////////////////////////////////////////////////////////////////////////


    public void applyTranslationStatus() {
        setTranslationUiState();

        if (translation == null) { // means not translated message
            setNotTranslated();
            return;
        }

        switch (translation.getTranslateStatus()) {
            case ERROR:
                setTranslationError();
                break;
            case TRANSLATING:
                setTranslating();
                break;
            case TRANSLATED:
                setIsTranslated();
                break;
            case REVERTED:
                setNotTranslated();
                break;
        }
    }

    private void setTranslationUiState(){
        int status = translation == null ? -10 : translation.getTranslateStatus();
//        iconTranslation.setVisibility(userLocale.equals(message.getLocale())
//                || status == TRANSLATING ? GONE : VISIBLE);
        translationProgress.setVisibility(status == TRANSLATING ? VISIBLE : GONE);
        messageTextView.setVisibility(status == TRANSLATING ? INVISIBLE : VISIBLE);
        translationStatus.setVisibility(status == TRANSLATING ? GONE : VISIBLE);
    }

    public void setTranslating(){
        messageTextView.setText(message.getText());
    }

    public void setNotTranslated() {
        translationStatus.setVisibility(GONE);
        messageTextView.setText(message.getText());

        //applyPositionOfTranslateIcon();
    }

    public void setTranslationError() {
        translationStatus.setText(itemView.getResources().getText(R.string.translate_error));
        translationStatus.setTextColor(itemView.getResources().getColor(R.color.translation_state_error));

        messageTextView.setText(TruncateUtils.truncate(message.getText(),
                messageTextView.getResources().getInteger(R.integer.messenger_max_message_length)));

       // applyPositionOfTranslateIcon();
    }

    public void setIsTranslated() {
        translationStatus.setText(itemView.getResources().getString(R.string.translate_from));
        translationStatus.setTextColor(itemView.getResources().getColor(R.color.translation_state_translated));

        messageTextView.setText(TruncateUtils.truncate(translation.getTranslation(),
                messageTextView.getResources().getInteger(R.integer.messenger_max_message_length)));

        //applyPositionOfTranslateIcon();
    }

//    private void applyPositionOfTranslateIcon() {
//        messageTextView.post(() -> {
//            int messageLines = messageTextView.getLineCount();
//            if (messageLines == 1) messageLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//            else messageLinearLayout.setOrientation(LinearLayout.VERTICAL);
//        });
//    }

}