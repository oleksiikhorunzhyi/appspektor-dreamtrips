package com.messenger.ui.view.add_member;

import android.content.Context;
import android.util.AttributeSet;

import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;

public class AddChatMembersScreenImpl extends ChatMembersScreenImpl<ExistingChatPath> {

    public AddChatMembersScreenImpl(Context context) {
        super(context);
    }

    public AddChatMembersScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public AddChatMembersScreenPresenterImpl createPresenter() {
        return new AddChatMembersScreenPresenterImpl(getContext(), injector, getPath().getConversationId());
    }
}
