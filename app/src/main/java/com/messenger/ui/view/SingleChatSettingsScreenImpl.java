package com.messenger.ui.view;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.presenter.ChatSettingsScreenPresenter;
import com.messenger.ui.presenter.SingleChatSettingsScreenPresenterImpl;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.OnClick;

public class SingleChatSettingsScreenImpl extends ChatSettingsScreenImpl {

    public SingleChatSettingsScreenImpl(Context context, String conversationId) {
        super(context, conversationId);
    }

    public SingleChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initUi() {
        super.initUi();

        // TODO: 1/2/16
        leaveChatButton.setVisibility(GONE);

    }

    @OnClick(R.id.chat_settings_single_chat_avatar_view)
    public void onAvatarClick() {
        getPresenter().onConversationAvatarClick();
    }

    @Override
    public void setConversation(Conversation conversation) {
        super.setConversation(conversation);
        toolbarPresenter.setTitle(R.string.chat_settings_single_chat);
    }

    @Override
    public void setParticipants(Conversation conversation, List<User> participants) {
        singleChatAvatarView.setVisibility(View.VISIBLE);
        User addressee = participants.get(0);
        singleChatAvatarView.setImageURI(Uri.parse(addressee.getAvatarUrl()));
        chatNameTextView.setText(addressee.getName());
        chatDescriptionTextView.setText(addressee.isOnline()
                        ? R.string.chat_settings_single_chat_online
                        : R.string.chat_settings_single_chat_offline);
    }

    @Override
    protected int getLeaveChatButtonStringRes() {
        return R.string.chat_settings_row_delete_chat;
    }

    @NonNull
    @Override
    public ChatSettingsScreenPresenter createPresenter() {
        return new SingleChatSettingsScreenPresenterImpl(getActivity(), conversationId);
    }
}
