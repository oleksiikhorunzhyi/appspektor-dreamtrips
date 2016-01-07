package com.messenger.ui.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.presenter.ChatSettingsScreenPresenter;
import com.messenger.ui.presenter.SingleChatSettingsScreenPresenterImpl;
import com.messenger.util.Constants;
import com.squareup.picasso.Picasso;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.OnClick;

public class SingleChatSettingsScreenImpl extends ChatSettingsScreenImpl {

    public SingleChatSettingsScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleChatSettingsScreenImpl(Context context) {
        super(context);
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
        Picasso.with(getContext())
                .load(addressee.getAvatarUrl())
                .placeholder(Constants.PLACEHOLDER_USER_AVATAR_BIG)
                .into(singleChatAvatarView);
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
        Activity activity = getActivity();
        return new SingleChatSettingsScreenPresenterImpl(activity, activity.getIntent());
    }
}
