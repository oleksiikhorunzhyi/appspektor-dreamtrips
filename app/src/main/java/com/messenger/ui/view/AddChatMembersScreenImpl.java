package com.messenger.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;

public class AddChatMembersScreenImpl extends ChatMembersScreenImpl {

    public AddChatMembersScreenImpl(Context context, String conversationId) {
        super(context);
        this.conversationId = conversationId;
    }

    public AddChatMembersScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public AddChatMembersScreenPresenterImpl createPresenter() {
        return new AddChatMembersScreenPresenterImpl(getContext(), conversationId);
    }
}
